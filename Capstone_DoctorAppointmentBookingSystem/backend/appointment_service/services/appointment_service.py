# Core business logic for appointment booking, cancellation, history,
# and status management (FR-7, FR-9, FR-10, FR-15, FR-17, FR-18).
#
# Double-booking prevention strategy:
#   1. Application-level check: slot.status == AVAILABLE before writing.
#   2. Database-level guarantee: unique compound index on (doctor_id, slot_id)
#      in the appointments collection rejects any concurrent duplicate insert
#      with a DuplicateKeyError, which is caught and mapped to SlotAlreadyBookedError.

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
from integrations.user_service_client import fetch_doctor, fetch_patient
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

# Allowed terminal status values a doctor can set (FR-17).
_DOCTOR_SETTABLE_STATUSES = {AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW}


def _to_appointment_response(
    appointment: Appointment,
    payment: Payment | None = None,
) -> AppointmentResponse:
    """Converts Appointment + optional Payment documents into the API response schema."""
    return AppointmentResponse(
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


def _to_card(appointment: Appointment) -> AppointmentCardResponse:
    """Converts an Appointment document into a lightweight card response."""
    return AppointmentCardResponse(
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


async def book_appointment(
    request: BookAppointmentRequest,
    current_user: CurrentUser,
) -> AppointmentResponse:
    """
    Books an appointment for the authenticated patient (FR-7).

    Steps:
      1. Validate appointment date is not in the past.
      2. Verify slot exists, belongs to the doctor, and is AVAILABLE.
      3. Fetch doctor and patient snapshots from User Service.
      4. Atomically mark slot BOOKED and insert appointment.
         The unique DB index on (doctor_id, slot_id) is the final
         concurrency guard against double booking.
      5. Create a PENDING payment record (FR-8).
    """
    # Step 1 — future date check
    if request.appointment_date < date.today():
        raise PastAppointmentDateError()

    # Step 2 — slot validation
    slot_id = PydanticObjectId(request.slot_id)
    doctor_id = PydanticObjectId(request.doctor_id)

    slot = await get_slot_by_id(slot_id)
    if slot is None:
        raise SlotNotFoundException(request.slot_id)
    if str(slot.doctor_id) != request.doctor_id:
        raise SlotNotFoundException(request.slot_id)
    if slot.status != SlotStatus.AVAILABLE:
        raise SlotAlreadyBookedError()

    # Step 3 — fetch snapshots from User Service
    doctor_data = await fetch_doctor(request.doctor_id)
    patient_data = await fetch_patient(current_user.user_id)

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

    # Step 4 — atomically mark slot BOOKED and insert appointment
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

    # Step 5 — create PENDING payment
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
    """
    Cancels a CONFIRMED appointment (FR-9).

    Rules:
      - Appointment must belong to the requesting patient.
      - Status must be CONFIRMED (not already cancelled/completed).
      - Cancellation must be at least 2 hours before appointment start.
      - Slot is immediately returned to AVAILABLE.
    """
    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    if str(appointment.patient_id) != current_user.user_id:
        raise AppointmentNotOwnedError()

    if appointment.status != AppointmentStatus.CONFIRMED:
        raise InvalidStatusTransitionError(appointment.status.value, "CANCELLED")

    # Build the full appointment datetime and enforce the 2-hour window (FR-9)
    appt_datetime = datetime.combine(
        appointment.appointment_date,
        datetime.strptime(appointment.start_time, "%H:%M").time(),
    ).replace(tzinfo=timezone.utc)

    if datetime.now(timezone.utc) >= appt_datetime - timedelta(hours=2):
        raise CancellationWindowExpiredError()

    # Return slot to AVAILABLE
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
    """Returns appointment history for the authenticated patient (FR-10)."""
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
    """
    Returns appointments for the authenticated doctor (FR-15).

    view options:
      - today    : today's appointments regardless of status
      - upcoming : future CONFIRMED appointments
      - all      : all appointments, optionally filtered by date
    """
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
    """
    Allows a doctor to mark an appointment COMPLETED or NO_SHOW (FR-17).

    Rules:
      - Only the doctor linked to the appointment may update status.
      - Only CONFIRMED appointments can be updated.
      - Only COMPLETED and NO_SHOW are valid target statuses.
      - The appointment time must have already passed.
    """
    if request.status not in _DOCTOR_SETTABLE_STATUSES:
        raise InvalidStatusTransitionError("CONFIRMED", request.status.value)

    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    if str(appointment.doctor_id) != current_user.user_id:
        raise AppointmentNotOwnedError()

    if appointment.status != AppointmentStatus.CONFIRMED:
        raise InvalidStatusTransitionError(appointment.status.value, request.status.value)

    # Appointment time must have passed (FR-17)
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
    """
    Returns full appointment detail (FR-18).
    Accessible by the patient or the doctor linked to the appointment.
    """
    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    is_patient = str(appointment.patient_id) == current_user.user_id
    is_doctor = str(appointment.doctor_id) == current_user.user_id

    if not is_patient and not is_doctor:
        raise AppointmentNotOwnedError()

    payment = await get_payment_by_appointment_id(appointment.id)
    return _to_appointment_response(appointment, payment)