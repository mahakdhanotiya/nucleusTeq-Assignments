import re
from datetime import date as DateType
from typing import Optional
 
from pydantic import BaseModel, Field, field_validator, model_validator
from constants.message_constants import INVALID_TIME_FORMAT, END_TIME_MUST_BE_AFTER_START
 
 # Shared helper used by both create and update request validators.
def _is_valid_time(value: str) -> bool:
    """Returns True if the value matches HH:MM 24-hour format."""
    return bool(re.fullmatch(r"^([01]\d|2[0-3]):[0-5]\d$", value))
 
 
class CreateSlotRequest(BaseModel):
    """Request schema for creating a slot."""
 
    slot_date: DateType = Field(..., description="The date of the slot (YYYY-MM-DD).", alias="date")
    start_time: str = Field(
        ..., description="Start time in HH:MM 24-hour format (e.g. '09:00')."
    )
    end_time: str = Field(
        ..., description="End time in HH:MM 24-hour format (e.g. '09:30')."
    )
    
    model_config = {"populate_by_name": True}
 
    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        """Ensures both times are in valid HH:MM 24-hour format."""
        if not _is_valid_time(value):
            raise ValueError(INVALID_TIME_FORMAT)
        return value
 
    @model_validator(mode="after")
    def validate_end_after_start(self) -> "CreateSlotRequest":
        """Validates the slot time range."""
        if self.start_time and self.end_time:
            if self.end_time <= self.start_time:
                raise ValueError(END_TIME_MUST_BE_AFTER_START)
        return self
 
 
class UpdateSlotRequest(BaseModel):
    """Request schema for updating a slot."""
 
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
            raise ValueError(INVALID_TIME_FORMAT)
        return value
 
    @model_validator(mode="after")
    def validate_end_after_start(self) -> "UpdateSlotRequest":
        """Validates the slot time range."""
        if self.start_time and self.end_time:
            if self.end_time <= self.start_time:
                raise ValueError(END_TIME_MUST_BE_AFTER_START)
        return self
 