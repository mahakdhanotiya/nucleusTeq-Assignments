import uuid
from datetime import datetime, timezone

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import ASCENDING, IndexModel

from enums.payment_status import PaymentStatus


class Payment(Document):
    """MongoDB document for a mock payment record linked to one appointment."""

    appointment_id: PydanticObjectId
    patient_id: PydanticObjectId
    amount: float
    status: PaymentStatus = Field(default=PaymentStatus.PENDING)
    transaction_ref: str = Field(default_factory=lambda: str(uuid.uuid4()))

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "payments"
        indexes = [
            IndexModel([("appointment_id", ASCENDING)], name="appointment_id_index"),
            IndexModel(
                [("patient_id", ASCENDING), ("status", ASCENDING)],
                name="patient_status_index",
            ),
        ]