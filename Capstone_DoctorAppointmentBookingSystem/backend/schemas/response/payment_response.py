from datetime import datetime

from pydantic import BaseModel

from enums.payment_status import PaymentStatus


class PaymentResponse(BaseModel):
    """Full payment record returned after processing."""

    id: str
    appointment_id: str
    patient_id: str
    amount: float
    status: PaymentStatus
    transaction_ref: str
    created_at: datetime
    updated_at: datetime