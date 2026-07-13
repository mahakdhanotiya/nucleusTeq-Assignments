from datetime import date as datetime_date, datetime
from typing import Optional
from pydantic import BaseModel, Field

from enums.request_status import RequestStatus


class DoctorCancellationResponseSchema(BaseModel):
    """Response schema for a bulk cancellation request."""

    id: str
    doctor_id: str
    doctor_name: Optional[str] = None
    doctor_email: Optional[str] = None
    date: datetime_date
    start_time: Optional[str] = None
    end_time: Optional[str] = None
    reason: str
    status: RequestStatus
    created_at: datetime
    updated_at: datetime
