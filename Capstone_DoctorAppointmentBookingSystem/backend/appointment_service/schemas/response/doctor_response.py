# Response schemas for doctor-facing endpoints in Appointment Service.
# These are composed from User Service profile data + local slot data.

from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from enums.slot_status import SlotStatus


class SlotSummary(BaseModel):
    """Minimal slot representation embedded inside a doctor detail response."""

    id: str
    date: date
    start_time: str
    end_time: str
    status: SlotStatus


class DoctorSearchResult(BaseModel):
    """
    Single doctor card returned by GET /doctors/search (FR-5).
    Composed from User Service profile + available slot count from local DB.
    """

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
    """
    Full doctor profile returned by GET /doctors/{user_id} (FR-6).
    Includes all profile fields from User Service plus the doctor's
    available slots from the local slots collection.
    """

    user_id: str
    full_name: str
    specialization: Optional[str] = None
    qualification: Optional[str] = None
    experience_years: Optional[int] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None
    available_slots: list[SlotSummary] = []