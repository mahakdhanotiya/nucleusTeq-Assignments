from datetime import date as datetime_date
import re
from pydantic import BaseModel, Field, field_validator


class DoctorCancellationRequestSchema(BaseModel):
    """Request schema for submitting a bulk cancellation request."""

    date: datetime_date = Field(..., description="Date for cancellation (YYYY-MM-DD).")
    start_time: str = Field(..., description="Start of cancellation time window (HH:MM).")
    end_time: str = Field(..., description="End of cancellation time window (HH:MM).")
    reason: str = Field(..., min_length=5, description="Reason for the cancellation.")

    @field_validator("date")
    @classmethod
    def validate_date(cls, value: datetime_date) -> datetime_date:
        from validators.appointment_validators import validate_cancellation_date
        return validate_cancellation_date(value)

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time(cls, value: str) -> str:
        from validators.appointment_validators import validate_cancellation_time
        return validate_cancellation_time(value)
