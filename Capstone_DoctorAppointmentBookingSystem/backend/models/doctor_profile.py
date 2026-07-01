from datetime import datetime, timezone
from typing import Optional

from beanie import Document, Indexed, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel, ASCENDING


class DoctorProfile(Document):
    """MongoDB document for doctor-specific professional data."""

    user_id: Indexed(PydanticObjectId, unique=True)
    qualification: str
    specialization: str
    experience_years: int = Field(..., ge=0)
    license_number: Indexed(str, unique=True)
    consultation_fee: float = Field(default=0.0, ge=0)
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "doctor_profiles"
        indexes = [
            IndexModel([("specialization", ASCENDING)], name="specialization_index"),
        ]