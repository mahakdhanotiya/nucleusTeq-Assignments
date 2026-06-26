# Response schemas for slot-related API endpoints.
# These control exactly what the API sends back — never the raw Beanie document.

from datetime import date, datetime

from pydantic import BaseModel

from enums.slot_status import SlotStatus


class SlotResponse(BaseModel):
    """
    Represents a single slot in any API response.
    Used by all slot endpoints: create, list, update.
    """

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
    """Generic confirmation response for actions that return no resource."""

    success: bool = True
    message: str