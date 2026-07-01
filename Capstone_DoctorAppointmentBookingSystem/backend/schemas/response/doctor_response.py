from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from enums.slot_status import SlotStatus


class SlotSummary(BaseModel):
    """Response schema for slot details."""

    id: str
    date: date
    start_time: str
    end_time: str
    status: SlotStatus


class DoctorSearchResult(BaseModel):
    """Response schema for doctor search results."""

    user_id: str
    full_name: str
    specialization: Optional[str] = None
    qualification: Optional[str] = None
    experience_years: Optional[int] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None
    available_slot_count: int = 0


class DoctorDetailResponse(BaseModel):
    """Response schema for doctor details."""

    user_id: str
    full_name: str
    specialization: Optional[str] = None
    qualification: Optional[str] = None
    experience_years: Optional[int] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None
    available_slots: list[SlotSummary] = []