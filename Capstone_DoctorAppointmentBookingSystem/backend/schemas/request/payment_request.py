from pydantic import BaseModel, Field
from constants.payment_constants import DEFAULT_PAYMENT_METHOD


class ProcessPaymentRequest(BaseModel):
    """Request schema for processing a payment."""

    payment_method: str = Field(
        default=DEFAULT_PAYMENT_METHOD,
        description="Simulated payment method. No real gateway is used.",
    )