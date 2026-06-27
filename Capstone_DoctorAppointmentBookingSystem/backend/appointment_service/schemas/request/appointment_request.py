from datetime import date
from typing import Optional

from pydantic import BaseModel, Field

from enums.appointment_status import AppointmentStatus


class BookAppointmentRequest(BaseModel):
    """Request body for POST /appointments — patient books a slot (FR-7)."""

    doctor_id: str = Field(..., description="User ID of the doctor.")
    slot_id: str = Field(..., description="ID of the slot to book.")
    appointment_date: date = Field(..., description="Date of the appointment (YYYY-MM-DD).")


class UpdateAppointmentStatusRequest(BaseModel):
    """Request body for PATCH /appointments/{id}/status — doctor marks outcome (FR-17)."""

    status: AppointmentStatus = Field(
        ...,
        description="New status. Doctor can set COMPLETED or NO_SHOW only.",
    )


class CancelAppointmentRequest(BaseModel):
    """Request body for PATCH /appointments/{id}/cancel — patient cancels (FR-9)."""

    reason: Optional[str] = Field(default=None, description="Optional cancellation reason.")