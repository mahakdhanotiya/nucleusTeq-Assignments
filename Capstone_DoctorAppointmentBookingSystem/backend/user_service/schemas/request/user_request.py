import re
from typing import Optional

from pydantic import BaseModel, Field, field_validator


class UpdateProfileRequest(BaseModel):
    """Request body for PUT /users/me. All fields optional; only provided fields are updated."""

    full_name: Optional[str] = Field(default=None, min_length=2)
    phone_number: Optional[str] = None

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not re.fullmatch(r"[A-Za-z\s]+", value):
            raise ValueError("Full name must contain only alphabets and spaces.")
        return value.strip() if value else value

    @field_validator("phone_number")
    @classmethod
    def validate_phone_number(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not re.fullmatch(r"\d{10}", value):
            raise ValueError("Phone number must be exactly 10 digits.")
        return value


class ChangePasswordRequest(BaseModel):
    """Request body for PUT /users/change-password."""

    old_password: str
    new_password: str = Field(..., min_length=8, max_length=12)

    @field_validator("new_password")
    @classmethod
    def validate_new_password(cls, value: str) -> str:
        if not re.search(r"[A-Z]", value):
            raise ValueError("Password must contain at least one uppercase letter.")
        if not re.search(r"[!@#$%^&*(),.?\":{}|<>_\-+=]", value):
            raise ValueError("Password must contain at least one special character.")
        return value


class UpdateDoctorProfileRequest(BaseModel):
    """
    Request body for PUT /users/me/doctor-profile when the authenticated user is a DOCTOR.
    Covers FR-16: Update Qualification, Consultation Fee, Clinic Address, Profile Photo.
    All fields are optional — only provided fields are updated.
    """

    qualification: Optional[str] = Field(default=None, min_length=2)
    consultation_fee: Optional[float] = Field(default=None, ge=0)
    clinic_address: Optional[str] = Field(default=None, min_length=2)
    profile_photo_url: Optional[str] = Field(default=None, min_length=1)