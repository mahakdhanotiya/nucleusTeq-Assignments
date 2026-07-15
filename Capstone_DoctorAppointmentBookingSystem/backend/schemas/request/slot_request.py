from datetime import date as DateType
from typing import Optional
 
from pydantic import BaseModel, Field, field_validator, model_validator
from validators.slot_validators import validate_time_format, validate_end_after_start
 
 
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
    def validate_times(cls, value: str) -> str:
        """Ensures both times are in valid HH:MM 24-hour format."""
        return validate_time_format(value)
 
    @model_validator(mode="after")
    def validate_end_after_start_time(self) -> "CreateSlotRequest":
        """Validates the slot time range."""
        validate_end_after_start(self.start_time, self.end_time)
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
        if value is not None:
            return validate_time_format(value)
        return value
 
    @model_validator(mode="after")
    def validate_end_after_start_time(self) -> "UpdateSlotRequest":
        """Validates the slot time range."""
        if self.start_time and self.end_time:
            validate_end_after_start(self.start_time, self.end_time)
        return self
 