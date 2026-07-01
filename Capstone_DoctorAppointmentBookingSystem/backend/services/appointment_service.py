import logging
from datetime import date, datetime, timezone, timedelta

from beanie import PydanticObjectId
from pymongo.errors import DuplicateKeyError

from dependencies.auth_dependency import CurrentUser
from enums.appointment_status import AppointmentStatus
from enums.payment_status import PaymentStatus
from enums.slot_status import SlotStatus
from exceptions.appointment_exceptions import (
    AppointmentNotFoundException,
    AppointmentNotOwnedError,
    AppointmentNotCompletedYetError,
    CancellationWindowExpiredError,
    InvalidStatusTransitionError,
    PastAppointmentDateError,
    SlotAlreadyBookedError,
    SlotNotFoundException,
)
from services.user_service import internal_fetch_doctor, internal_fetch_patient
from models.appointment import Appointment, DoctorSnapshot, PatientSnapshot
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
    DoctorSnapshotResponse,
    PatientSnapshotResponse,
    PaymentSummary,
)

logger = logging.getLogger(__name__)

# Allowed terminal status values a doctor can set.
_DOCTOR_SETTABLE_STATUSES = {AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW}


def _to_appointment_response(
    appointment: Appointment,
    payment: Payment | None = None,
) -> AppointmentResponse:
    """Builds an appointment response."""
    appointment_response = AppointmentResponse(
        id=str(appointment.id),
        patient_id=str(appointment.patient_id),
        doctor_id=str(appointment.doctor_id),
        slot_id=str(appointment.slot_id),
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        doctor_snapshot=DoctorSnapshotResponse(**appointment.doctor_snapshot.model_dump()),
        patient_snapshot=PatientSnapshotResponse(**appointment.patient_snapshot.model_dump()),
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
        doctor_name=appointment.doctor_snapshot.full_name,
        doctor_specialization=appointment.doctor_snapshot.specialization,
        patient_name=appointment.patient_snapshot.full_name,
        patient_phone=appointment.patient_snapshot.phone_number,
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

    doctor_data = await internal_fetch_doctor(request.doctor_id)
    patient_data = await internal_fetch_patient(current_user.user_id)

    doctor_snapshot = DoctorSnapshot(
        user_id=doctor_data["user_id"],
        full_name=doctor_data["full_name"],
        specialization=doctor_data.get("specialization"),
        consultation_fee=doctor_data.get("consultation_fee"),
        clinic_address=doctor_data.get("clinic_address"),
    )
    patient_snapshot = PatientSnapshot(
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
        doctor_snapshot=doctor_snapshot,
        patient_snapshot=patient_snapshot,
    )

    try:
        await create_appointment(appointment)
    except DuplicateKeyError:
        # Concurrent request booked the same slot — restore slot status
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
        raise AppointmentNotOwnedError()

    if appointment.status != AppointmentStatus.CONFIRMED:
        raise InvalidStatusTransitionError(appointment.status.value, "CANCELLED")

    appt_datetime = datetime.combine(
        appointment.appointment_date,
        datetime.strptime(appointment.start_time, "%H:%M").time(),
    ).replace(tzinfo=timezone.utc)

    if datetime.now(timezone.utc) >= appt_datetime - timedelta(hours=2):
        raise CancellationWindowExpiredError()

    slot = await get_slot_by_id(appointment.slot_id)
    if slot:
        slot.status = SlotStatus.AVAILABLE
        await update_slot(slot)

    appointment.status = AppointmentStatus.CANCELLED
    appointment.cancelled_at = datetime.now(timezone.utc)
    appointment.cancellation_reason = request.reason
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
        raise AppointmentNotOwnedError()

    if appointment.status != AppointmentStatus.CONFIRMED:
        raise InvalidStatusTransitionError(appointment.status.value, request.status.value)

    # Appointment time must have passed
    appt_datetime = datetime.combine(
        appointment.appointment_date,
        datetime.strptime(appointment.end_time, "%H:%M").time(),
    ).replace(tzinfo=timezone.utc)

    if datetime.now(timezone.utc) < appt_datetime:
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
        raise AppointmentNotOwnedError()

    payment = await get_payment_by_appointment_id(appointment.id)
    return _to_appointment_response(appointment, payment)