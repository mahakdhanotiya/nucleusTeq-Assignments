import re
from datetime import date
from typing import Optional

from pydantic import BaseModel, EmailStr, Field, field_validator, model_validator

from enums.gender import Gender
from enums.user_role import UserRole


class RegisterRequest(BaseModel):
    """Request schema for user registration."""

    full_name: str = Field(..., min_length=2)
    email: EmailStr
    password: str = Field(..., min_length=8, max_length=12)
    phone_number: str
    role: UserRole

    # Patient-specific fields
    gender: Optional[Gender] = None
    date_of_birth: Optional[date] = None

    # Doctor-specific fields
    qualification: Optional[str] = None
    specialization: Optional[str] = None
    experience_years: Optional[int] = Field(default=None, ge=0)
    license_number: Optional[str] = None

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: str) -> str:
        if not re.fullmatch(r"[A-Za-z\s]+", value):
            raise ValueError("Full name must contain only alphabets and spaces.")
        return value.strip()

    @field_validator("phone_number")
    @classmethod
    def validate_phone_number(cls, value: str) -> str:
        if not re.fullmatch(r"\d{10}", value):
            raise ValueError("Phone number must be exactly 10 digits.")
        return value

    @field_validator("password")
    @classmethod
    def validate_password(cls, value: str) -> str:
        if not re.search(r"[A-Z]", value):
            raise ValueError("Password must contain at least one uppercase letter.")

        if not re.search(r"\d", value):
            raise ValueError("Password must contain at least one digit.")

        if not re.search(r"[!@#$%^&*(),.?\":{}|<>_\-+=]", value):
            raise ValueError("Password must contain at least one special character.")

        return value

    @model_validator(mode="after")
    def validate_role_specific_fields(self) -> "RegisterRequest":
        if self.role == UserRole.PATIENT:
            if self.gender is None or self.date_of_birth is None:
                raise ValueError(
                    "Gender and date_of_birth are required for PATIENT registration."
                )

        if self.role == UserRole.DOCTOR:
            missing = [
                field_name
                for field_name, value in [
                    ("qualification", self.qualification),
                    ("specialization", self.specialization),
                    ("experience_years", self.experience_years),
                    ("license_number", self.license_number),
                ]
                if value is None
            ]

            if missing:
                raise ValueError(
                    f"Missing required DOCTOR fields: {', '.join(missing)}"
                )

        if self.role == UserRole.ADMIN:
            raise ValueError(
                "ADMIN accounts cannot be created through registration."
            )

        return self


class LoginRequest(BaseModel):
    """Request schema for user login."""

    email: EmailStr
    password: str