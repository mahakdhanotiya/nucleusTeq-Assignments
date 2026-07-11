"""Request schemas for the user management module."""

from typing import Optional

from pydantic import BaseModel, Field, field_validator

from validators.user_validators import (
    validate_full_name,
    validate_phone_number,
    validate_new_password,
)


class UpdateProfileRequest(BaseModel):
    """Request model for updating a user profile."""

    full_name: Optional[str] = Field(default=None, min_length=2)
    phone_number: Optional[str] = None

    @field_validator("full_name")
    @classmethod
    def check_full_name(cls, value: Optional[str]) -> Optional[str]:
        return validate_full_name(value)

    @field_validator("phone_number")
    @classmethod
    def check_phone_number(cls, value: Optional[str]) -> Optional[str]:
        return validate_phone_number(value)


class ChangePasswordRequest(BaseModel):
    """Request model for changing a password."""

    old_password: str
    new_password: str = Field(..., min_length=8, max_length=12)

    @field_validator("new_password")
    @classmethod
    def check_new_password(cls, value: str) -> str:
        return validate_new_password(value)


class UpdateDoctorProfileRequest(BaseModel):
    """Request model for updating a doctor's professional profile."""

    qualification: Optional[str] = Field(default=None, min_length=2)
    experience_years: Optional[int] = Field(default=None, ge=0)
    license_number: Optional[str] = Field(default=None, min_length=2)
    consultation_fee: Optional[float] = Field(default=None, ge=0)
    clinic_address: Optional[str] = Field(default=None, min_length=2)
    profile_photo_url: Optional[str] = Field(default=None, min_length=1)