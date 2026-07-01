import re
from typing import Optional

from pydantic import BaseModel, Field, field_validator
from constants.message_constants import (
    FULL_NAME_VALIDATION_ERROR,
    PHONE_NUMBER_VALIDATION_ERROR,
    PASSWORD_UPPERCASE_ERROR,
    PASSWORD_SPECIAL_CHAR_ERROR,
)


class UpdateProfileRequest(BaseModel):
    """Request model for updating a user profile."""

    full_name: Optional[str] = Field(default=None, min_length=2)
    phone_number: Optional[str] = None

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not re.fullmatch(r"[A-Za-z\s]+", value):
            raise ValueError(FULL_NAME_VALIDATION_ERROR)
        return value.strip() if value else value

    @field_validator("phone_number")
    @classmethod
    def validate_phone_number(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not re.fullmatch(r"\d{10}", value):
            raise ValueError(PHONE_NUMBER_VALIDATION_ERROR)
        return value


class ChangePasswordRequest(BaseModel):
    """Request model for changing a password."""

    old_password: str
    new_password: str = Field(..., min_length=8, max_length=12)

    @field_validator("new_password")
    @classmethod
    def validate_new_password(cls, value: str) -> str:
        if not re.search(r"[A-Z]", value):
            raise ValueError(PASSWORD_UPPERCASE_ERROR)
        if not re.search(r"[!@#$%^&*(),.?\":{}|<>_\-+=]", value):
            raise ValueError(PASSWORD_SPECIAL_CHAR_ERROR)
        return value


class UpdateDoctorProfileRequest(BaseModel):
    """Request model for updating a doctor's profile."""

    qualification: Optional[str] = Field(default=None, min_length=2)
    consultation_fee: Optional[float] = Field(default=None, ge=0)
    clinic_address: Optional[str] = Field(default=None, min_length=2)
    profile_photo_url: Optional[str] = Field(default=None, min_length=1)