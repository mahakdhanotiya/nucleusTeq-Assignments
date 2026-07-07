from datetime import date
from typing import Optional

from pydantic import BaseModel, Field

from enums.appointment_status import AppointmentStatus


class BookAppointmentRequest(BaseModel):
    """Request schema for booking a new appointment."""

    doctor_id: str = Field(..., description="User ID of the doctor.")
    slot_id: str = Field(..., description="ID of the slot to book.")
    appointment_date: date = Field(..., description="Date of the appointment (YYYY-MM-DD).")


class UpdateAppointmentStatusRequest(BaseModel):
    """Request schema for updating an appointment status."""

    status: AppointmentStatus = Field(
        ...,
        description="New status. Doctor can set COMPLETED or NO_SHOW only.",
    )


class CancelAppointmentRequest(BaseModel):
    """Request schema for cancelling an existing appointment."""

    reason: Optional[str] = Field(default=None, description="Optional cancellation reason.")