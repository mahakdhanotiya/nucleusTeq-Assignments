from datetime import date, datetime, timezone
from typing import Optional
from beanie import Document, PydanticObjectId
from pydantic import Field

from enums.request_status import RequestStatus


class DoctorCancellationRequest(Document):
    """
    Beanie model for a doctor's request to bulk cancel appointments.
    start_time and end_time must be in 'HH:MM' format.
    """

    doctor_id: PydanticObjectId
    date: date
    start_time: Optional[str] = None
    end_time: Optional[str] = None
    cut_off_time: Optional[str] = None
    reason: str
    status: RequestStatus = Field(default=RequestStatus.PENDING)

    created_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )
    updated_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )

    class Settings:
        name = "doctor_cancellation_requests"
