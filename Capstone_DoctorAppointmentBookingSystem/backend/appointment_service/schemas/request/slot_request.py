# The Slot Beanie Document (models/slot.py) defines the database shape.
# These schemas define the wire format and client-facing validation rules.

import re
from datetime import date as DateType
from typing import Optional
 
from pydantic import BaseModel, Field, field_validator, model_validator
 
 # Shared helper used by both create and update request validators.
def _is_valid_time(value: str) -> bool:
    """Returns True if the value matches HH:MM 24-hour format."""
    return bool(re.fullmatch(r"^([01]\d|2[0-3]):[0-5]\d$", value))
 
 
class CreateSlotRequest(BaseModel):
    """
    Request body for POST /slots.
    Doctors provide the date and time window for a new availability slot.
    """
 
    slot_date: DateType = Field(..., description="The date of the slot (YYYY-MM-DD).", alias="date")
    start_time: str = Field(
        ..., description="Start time in HH:MM 24-hour format (e.g. '09:00')."
    )
    end_time: str = Field(
        ..., description="End time in HH:MM 24-hour format (e.g. '09:30')."
    )
    
    # Allows clients to send either "date" or the internal field name "slot_date".
    model_config = {"populate_by_name": True}
 
    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        """Ensures both times are in valid HH:MM 24-hour format."""
        if not _is_valid_time(value):
            raise ValueError(
                "Time must be in HH:MM 24-hour format (e.g. '09:00', '17:30')."
            )
        return value
 
    @model_validator(mode="after")
    def validate_end_after_start(self) -> "CreateSlotRequest":
        """
        Ensures end_time is strictly after start_time.
        The deeper business rule (past date check, overlap check) is
        enforced in slot_service.py where we have access to the database.
        """
        if self.start_time and self.end_time:
            if self.end_time <= self.start_time:
                raise ValueError("End time must be after start time.")
        return self
 
 
class UpdateSlotRequest(BaseModel):
    """
    Request body for PUT /slots/{slot_id}.
    All fields are optional — only provided fields are updated.
    A slot can only be updated if its status is AVAILABLE.
    """
 
    slot_date: Optional[DateType] = Field(default=None, description="New date for the slot.", alias="date")
    start_time: Optional[str] = Field(
        default=None, description="New start time in HH:MM format."
    )
    end_time: Optional[str] = Field(
        default=None, description="New end time in HH:MM format."
    )
 
    model_config = {"populate_by_name": True}
 
    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not _is_valid_time(value):
            raise ValueError(
                "Time must be in HH:MM 24-hour format (e.g. '09:00', '17:30')."
            )
        return value
 
    @model_validator(mode="after")
    def validate_end_after_start(self) -> "UpdateSlotRequest":
        """
        Only checks the end/start constraint when BOTH are provided in the request.
        If only one is provided, the service resolves the missing value from the
        existing slot before performing the check.
        """
        if self.start_time and self.end_time:
            if self.end_time <= self.start_time:
                raise ValueError("End time must be after start time.")
        return self
 