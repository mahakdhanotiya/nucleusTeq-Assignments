# Mock payment processing.
# No real payment gateway — simulates a successful payment state transition.

import logging

from beanie import PydanticObjectId

from dependencies.auth_dependency import CurrentUser
from enums.payment_status import PaymentStatus
from exceptions.appointment_exceptions import (
    AppointmentNotFoundException,
    AppointmentNotOwnedError,
)
from repositories.appointment_repository import get_appointment_by_id
from repositories.payment_repository import (
    get_payment_by_appointment_id,
    update_payment,
)
from schemas.response.payment_response import PaymentResponse

logger = logging.getLogger(__name__)


def _to_payment_response(payment) -> PaymentResponse:
    """Builds a payment response."""
    return PaymentResponse(
        id=str(payment.id),
        appointment_id=str(payment.appointment_id),
        patient_id=str(payment.patient_id),
        amount=payment.amount,
        status=payment.status,
        transaction_ref=payment.transaction_ref,
        created_at=payment.created_at,
        updated_at=payment.updated_at,
    )


async def process_payment(
    appointment_id: str,
    current_user: CurrentUser,
) -> PaymentResponse:
    """Processes the payment for an appointment."""
    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    if str(appointment.patient_id) != str(current_user.user_id):
        raise AppointmentNotOwnedError()

    payment = await get_payment_by_appointment_id(appointment.id)
    if payment is None:
        raise AppointmentNotFoundException(appointment_id)

    payment.status = PaymentStatus.SUCCESS
    updated = await update_payment(payment)

    logger.info(
        f"Payment processed: appointment={appointment_id}, "
        f"patient={current_user.user_id}, ref={payment.transaction_ref}"
    )
    return _to_payment_response(updated)


async def get_payment_for_appointment(
    appointment_id: str,
    current_user: CurrentUser,
) -> PaymentResponse:
    """Returns the payment record for an appointment."""
    appointment = await get_appointment_by_id(PydanticObjectId(appointment_id))
    if appointment is None:
        raise AppointmentNotFoundException(appointment_id)

    is_patient = str(appointment.patient_id) == str(current_user.id)
    is_doctor = str(appointment.doctor_id) == str(current_user.id)
    if not is_patient and not is_doctor:
        raise AppointmentNotOwnedError()

    payment = await get_payment_by_appointment_id(appointment.id)
    if payment is None:
        raise AppointmentNotFoundException(appointment_id)

    return _to_payment_response(payment)