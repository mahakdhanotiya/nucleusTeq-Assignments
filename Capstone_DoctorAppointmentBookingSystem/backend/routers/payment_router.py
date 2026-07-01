# Payment endpoints for mock payment processing.

from fastapi import APIRouter, Depends, status

from dependencies.auth_dependency import CurrentUser, get_current_user, require_patient
from schemas.request.payment_request import ProcessPaymentRequest
from schemas.response.payment_response import PaymentResponse
from services.payment_service import get_payment_for_appointment, process_payment

router = APIRouter(prefix="/payments", tags=["Payments"])


@router.post(
    "/{appointment_id}/process",
    response_model=PaymentResponse,
    status_code=status.HTTP_200_OK,
    summary="Process mock payment for an appointment",
)
async def process(
    appointment_id: str,
    request: ProcessPaymentRequest,
    current_user: CurrentUser = Depends(require_patient),
) -> PaymentResponse:
    """Processes the payment for an appointment."""
    return await process_payment(appointment_id, current_user)


@router.get(
    "/{appointment_id}",
    response_model=PaymentResponse,
    status_code=status.HTTP_200_OK,
    summary="Get payment status for an appointment",
)
async def get_payment(
    appointment_id: str,
    current_user: CurrentUser = Depends(get_current_user),
) -> PaymentResponse:
    """Returns the payment record for a given appointment."""
    return await get_payment_for_appointment(appointment_id, current_user)