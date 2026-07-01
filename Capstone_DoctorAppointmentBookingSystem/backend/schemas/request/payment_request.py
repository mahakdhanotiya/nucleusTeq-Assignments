from pydantic import BaseModel, Field


class ProcessPaymentRequest(BaseModel):
    """Request schema for processing a payment."""

    payment_method: str = Field(
        default="MOCK_CARD",
        description="Simulated payment method. No real gateway is used.",
    )