from datetime import date, datetime, timezone

from beanie import Document, Indexed, PydanticObjectId
from pydantic import Field

from enums.gender import Gender


class PatientProfile(Document):
    """MongoDB document for patient-specific profile data."""

    user_id: Indexed(PydanticObjectId, unique=True)
    gender: Gender
    date_of_birth: date

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "patient_profiles"