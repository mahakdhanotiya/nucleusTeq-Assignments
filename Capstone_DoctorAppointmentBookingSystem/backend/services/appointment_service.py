import logging
from datetime import date, datetime, timezone, timedelta
from typing import Optional

from beanie import PydanticObjectId
from pymongo.errors import DuplicateKeyError

from dependencies.auth_dependency import CurrentUser
from enums.appointment_status import AppointmentStatus
from enums.payment_status import PaymentStatus
from enums.slot_status import SlotStatus
from enums.request_status import RequestStatus
from exceptions.custom_exceptions import (
    AppointmentNotFoundException,
    AppointmentAccessDeniedError,
    AppointmentNotCompletedYetError,
    CancellationWindowExpiredError,
    InvalidStatusTransitionError,
    PastAppointmentDateError,
    SlotAlreadyBookedError,
    SlotNotFoundException,
)
from models.cancellation_request import DoctorCancellationRequest
from schemas.request.cancellation_request import DoctorCancellationRequestSchema
from schemas.response.cancellation_response import DoctorCancellationResponseSchema
from repositories.cancellation_repository import (
    create_cancellation_request,
    get_cancellation_request_by_id,
    list_cancellation_requests,
    update_cancellation_request,
)
from services.user_service import internal_fetch_doctor, internal_fetch_patient
from models.appointment import Appointment, DoctorDetails, PatientDetails
from models.payment import Payment
from repositories.appointment_repository import (
    create_appointment,
    get_appointment_by_id,
    get_appointments_by_patient,
    get_appointments_by_doctor,
    get_todays_appointments_by_doctor,
    get_upcoming_appointments_by_doctor,
    update_appointment,
)
from repositories.payment_repository import create_payment, get_payment_by_appointment_id
from repositories.slot_repository import get_slot_by_id, update_slot
from schemas.request.appointment_request import (
    BookAppointmentRequest,
    CancelAppointmentRequest,
    UpdateAppointmentStatusRequest,
)
from schemas.response.appointment_response import (
    AppointmentCardResponse,
    AppointmentResponse,
    DoctorDetailsResponse,
    PatientDetailsResponse,
    PaymentSummary,
)

logger = logging.getLogger(__name__)

_DOCTOR_SETTABLE_STATUSES = {AppointmentStatus.COMPLETED, AppointmentStatus.ABSENT}


def _to_appointment_response(
    appointment: Appointment,
    payment: Optional[Payment] = None,
) -> AppointmentResponse:
    """Helper to map a DB Appointment Beanie document into the response DTO."""
    
    appointment_response = AppointmentResponse(
        id=str(appointment.id),
        patient_id=str(appointment.patient_id),
        doctor_id=str(appointment.doctor_id),
        slot_id=str(appointment.slot_id),
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        doctor_details=DoctorDetailsResponse(**appointment.doctor_details.model_dump()),
        patient_details=PatientDetailsResponse(**appointment.patient_details.model_dump()),
        payment=PaymentSummary(
            payment_id=str(payment.id),
            status=payment.status,
            amount=payment.amount,
            transaction_ref=payment.transaction_ref,
        ) if payment else None,
        cancelled_at=appointment.cancelled_at,
        cancellation_reason=appointment.cancellation_reason,
        created_at=appointment.created_at,
        updated_at=appointment.updated_at,
    )
    return appointment_response


def _to_card(appointment: Appointment) -> AppointmentCardResponse:
    """Builds an appointment card response."""
    appointment_card_response = AppointmentCardResponse(
        id=str(appointment.id),
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        doctor_name=appointment.doctor_details.full_name,
        doctor_specialization=appointment.doctor_details.specialization,
        patient_name=appointment.patient_details.full_name,
        patient_phone=appointment.patient_details.phone_number,
        cancellation_reason=appointment.cancellation_reason,
    )
    return appointment_card_response


async def book_appointment(
    request: BookAppointmentRequest,
    current_user: CurrentUser,
) -> AppointmentResponse:
    """Books an appointment for the authenticated patient."""
    
    if request.appointment_date < date.today():
        raise PastAppointmentDateError()

    slot_id = PydanticObjectId(request.slot_id)
    doctor_id = PydanticObjectId(request.doctor_id)

    slot = await get_slot_by_id(slot_id)
    if slot is None:
        raise SlotNotFoundException(request.slot_id)
    if str(slot.doctor_id) != request.doctor_id:
        raise SlotNotFoundException(request.slot_id)
    if slot.status != SlotStatus.AVAILABLE:
        raise SlotAlreadyBookedError()

    slot_date = slot.date.date() if isinstance(slot.date, datetime) else slot.date
    request_date = request.appointment_date.date() if isinstance(request.appointment_date, datetime) else request.appointment_date

    if slot_date != request_date:
        raise PastAppointmentDateError()

    slot_datetime = datetime.combine(
        slot_date,
        datetime.strptime(slot.start_time, "%H:%M").time(),
    )
    if datetime.now() >= slot_datetime:
        raise PastAppointmentDateError()

    doctor_data = await internal_fetch_doctor(request.doctor_id)
    patient_data = await internal_fetch_patient(current_user.user_id)

    doctor_details = DoctorDetails(
        user_id=doctor_data["user_id"],
        full_name=doctor_data["full_name"],
        specialization=doctor_data.get("specialization"),
        consultation_fee=doctor_data.get("consultation_fee"),
        clinic_address=doctor_data.get("clinic_address"),
    )
    patient_details = PatientDetails(
        user_id=patient_data["user_id"],
        full_name=patient_data["full_name"],
        phone_number=patient_data["phone_number"],
    )

    slot.status = SlotStatus.BOOKED
    await update_slot(slot)

    appointment = Appointment(
        patient_id=PydanticObjectId(current_user.user_id),
        doctor_id=doctor_id,
        slot_id=slot_id,
        appointment_date=request.appointment_date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        doctor_details=doctor_details,
        patient_details=patient_details,
    )

    try:
        await create_appointment(appointment)
    except DuplicateKeyError:
        slot.status = SlotStatus.AVAILABLE
        await update_slot(slot)
        raise SlotAlreadyBookedError()

    payment = Payment(
        appointment_id=appointment.id,
        patient_id=PydanticObjectId(current_user.user_id),
        amount=doctor_data.get("consultation_fee") or 0.0,
    )
    payment = await create_payment(payment)

    appointment.payment_id = payment.id
    await update_appointment(appointment)

    logger.info(
        f"Appointment booked: patient={current_user.user_id}, "
        f"doctor={request.doctor_id}, date={request.appointment_date}"
    )
    return _to_appointment_response(appointment, payment)


async def cancel_appointment(
    appointment_id: str,
    request: CancelAppointmentRequest,
    current_user: CurrentUser,
) -> AppointmentResponse:
    """Cancels a CONFIRMED appointment."""
    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    if str(appointment.patient_id) != str(current_user.user_id):
        raise AppointmentAccessDeniedError()

    if appointment.status != AppointmentStatus.CONFIRMED:
        raise InvalidStatusTransitionError(appointment.status.value, "CANCELLED")

    appt_datetime = datetime.combine(
        appointment.appointment_date,
        datetime.strptime(appointment.start_time, "%H:%M").time(),
    )

    if datetime.now() >= appt_datetime - timedelta(hours=2):
        raise CancellationWindowExpiredError()

    slot = await get_slot_by_id(appointment.slot_id)
    if slot:
        slot.status = SlotStatus.AVAILABLE
        await update_slot(slot)

    appointment.status = AppointmentStatus.CANCELLED
    appointment.cancelled_at = datetime.now(timezone.utc)
    appointment.cancellation_reason = request.reason
    appointment.active = None
    await update_appointment(appointment)

    payment = await get_payment_by_appointment_id(appointment.id)

    logger.info(f"Appointment cancelled: id={appointment_id}, patient={current_user.user_id}")
    return _to_appointment_response(appointment, payment)


async def get_patient_appointments(
    current_user: CurrentUser,
    status: AppointmentStatus | None = None,
) -> list[AppointmentCardResponse]:
    """Returns appointment history for the authenticated patient."""
    appointments = await get_appointments_by_patient(
        patient_id=PydanticObjectId(current_user.user_id),
        status=status,
    )
    return [_to_card(a) for a in appointments]


async def get_doctor_appointments(
    current_user: CurrentUser,
    view: str = "upcoming",
    appointment_date: date | None = None,
) -> list[AppointmentCardResponse]:
    """Returns appointments for the authenticated doctor."""
    doctor_id = PydanticObjectId(current_user.user_id)

    if view == "today":
        appointments = await get_todays_appointments_by_doctor(doctor_id)
    elif view == "upcoming":
        appointments = await get_upcoming_appointments_by_doctor(doctor_id)
    else:
        appointments = await get_appointments_by_doctor(
            doctor_id=doctor_id,
            appointment_date=appointment_date,
        )

    return [_to_card(a) for a in appointments]


async def update_appointment_status(
    appointment_id: str,
    request: UpdateAppointmentStatusRequest,
    current_user: CurrentUser,
) -> AppointmentResponse:
    """Updates an appointment status."""
    if request.status not in _DOCTOR_SETTABLE_STATUSES:
        raise InvalidStatusTransitionError("CONFIRMED", request.status.value)

    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    if str(appointment.doctor_id) != str(current_user.user_id):
        raise AppointmentAccessDeniedError()

    if appointment.status != AppointmentStatus.CONFIRMED:
        raise InvalidStatusTransitionError(appointment.status.value, request.status.value)

    appt_datetime = datetime.combine(
        appointment.appointment_date,
        datetime.strptime(appointment.end_time, "%H:%M").time(),
    )

    if datetime.now() < appt_datetime:
        raise AppointmentNotCompletedYetError()

    appointment.status = request.status
    await update_appointment(appointment)

    payment = await get_payment_by_appointment_id(appointment.id)

    logger.info(
        f"Appointment status updated: id={appointment_id}, "
        f"status={request.status.value}, doctor={current_user.user_id}"
    )
    return _to_appointment_response(appointment, payment)


async def get_appointment_detail(
    appointment_id: str,
    current_user: CurrentUser,
) -> AppointmentResponse:
    """Returns full appointment detail."""
    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    is_patient = str(appointment.patient_id) == str(current_user.id)
    is_doctor = str(appointment.doctor_id) == str(current_user.id)

    if not is_patient and not is_doctor:
        raise AppointmentAccessDeniedError()

    payment = await get_payment_by_appointment_id(appointment.id)
    return _to_appointment_response(appointment, payment)


async def _to_cancellation_response(
    req: DoctorCancellationRequest,
) -> DoctorCancellationResponseSchema:
    from repositories.user_repository import get_user_by_id
    user = await get_user_by_id(req.doctor_id)
    start = req.start_time if req.start_time else getattr(req, "cut_off_time", None) or "00:00"
    end = req.end_time if req.end_time else "23:59"
    return DoctorCancellationResponseSchema(
        id=str(req.id),
        doctor_id=str(req.doctor_id),
        doctor_name=user.full_name if user else "Unknown Doctor",
        doctor_email=user.email if user else "N/A",
        date=req.date,
        start_time=start,
        end_time=end,
        reason=req.reason,
        status=req.status,
        created_at=req.created_at,
        updated_at=req.updated_at,
    )


async def submit_bulk_cancellation_request(
    request_dto: DoctorCancellationRequestSchema,
    current_user: CurrentUser,
) -> DoctorCancellationResponseSchema:
    """Submits a bulk cancellation request by a doctor."""
    doctor_id = PydanticObjectId(current_user.user_id)

    existing = await DoctorCancellationRequest.find_one(
        DoctorCancellationRequest.doctor_id == doctor_id,
        DoctorCancellationRequest.date == request_dto.date,
        DoctorCancellationRequest.start_time == request_dto.start_time,
        DoctorCancellationRequest.end_time == request_dto.end_time,
        DoctorCancellationRequest.status == RequestStatus.PENDING,
    )
    if existing:
        raise ValueError("A pending cancellation request for this date and time range already exists.")

    new_request = DoctorCancellationRequest(
        doctor_id=doctor_id,
        date=request_dto.date,
        start_time=request_dto.start_time,
        end_time=request_dto.end_time,
        reason=request_dto.reason,
        status=RequestStatus.PENDING,
    )
    await create_cancellation_request(new_request)
    logger.info(f"Doctor {current_user.user_id} submitted cancellation request for {request_dto.date} between {request_dto.start_time} and {request_dto.end_time}")
    return await _to_cancellation_response(new_request)


async def get_bulk_cancellation_requests(
    status: RequestStatus | None = None,
) -> list[DoctorCancellationResponseSchema]:
    """Lists all bulk cancellation requests (admin view)."""
    requests = await list_cancellation_requests(status)
    return [await _to_cancellation_response(r) for r in requests]


async def approve_bulk_cancellation_request(
    request_id: str,
) -> DoctorCancellationResponseSchema:
    """Approves a bulk cancellation request and cancels all affected appointments/slots."""
    req_obj_id = PydanticObjectId(request_id)
    req = await get_cancellation_request_by_id(req_obj_id)
    if req is None:
        raise ValueError("Cancellation request not found.")
    if req.status != RequestStatus.PENDING:
        raise ValueError(f"Request is already {req.status.value}.")

    from repositories.slot_repository import get_slots_by_doctor, delete_slot
    slots = await get_slots_by_doctor(req.doctor_id, req.date)

    start = req.start_time if req.start_time else getattr(req, "cut_off_time", None) or "00:00"
    end = req.end_time if req.end_time else "23:59"
    affected_slots = [
        s for s in slots if start <= s.start_time <= end
    ]

    for slot in affected_slots:
        if slot.status == SlotStatus.BOOKED:
            appointment = await Appointment.find_one(
                Appointment.slot_id == slot.id,
                Appointment.status == AppointmentStatus.CONFIRMED,
            )
            if appointment:
                appointment.status = AppointmentStatus.CANCELLED
                appointment.cancelled_at = datetime.now(timezone.utc)
                appointment.cancellation_reason = f"Cancelled due to doctor leave request: {req.reason}"
                appointment.active = None
                await update_appointment(appointment)

        await delete_slot(slot)

    req.status = RequestStatus.APPROVED
    req.updated_at = datetime.now(timezone.utc)
    await update_cancellation_request(req)

    logger.info(f"Admin approved leave request {request_id}. Cancelled {len(affected_slots)} slots.")
    return await _to_cancellation_response(req)


async def reject_bulk_cancellation_request(
    request_id: str,
) -> DoctorCancellationResponseSchema:
    """Rejects a bulk cancellation request."""
    req_obj_id = PydanticObjectId(request_id)
    req = await get_cancellation_request_by_id(req_obj_id)
    if req is None:
        raise ValueError("Cancellation request not found.")
    if req.status != RequestStatus.PENDING:
        raise ValueError(f"Request is already {req.status.value}.")

    req.status = RequestStatus.REJECTED
    req.updated_at = datetime.now(timezone.utc)
    await update_cancellation_request(req)

    logger.info(f"Admin rejected bulk cancellation request {request_id}.")
    return await _to_cancellation_response(req)