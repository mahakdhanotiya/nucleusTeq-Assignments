from datetime import date, datetime

from pydantic import BaseModel

from enums.slot_status import SlotStatus


class SlotResponse(BaseModel):
    """Response schema for slot information."""

    id: str
    doctor_id: str
    date: date
    start_time: str
    end_time: str
    status: SlotStatus
    created_at: datetime
    updated_at: datetime

    # Enables automatic conversion from Beanie document objects to response models.
    class Config:
        from_attributes = True


class MessageResponse(BaseModel):
    """Response schema for confirmation messages."""

    success: bool = True
    message: str