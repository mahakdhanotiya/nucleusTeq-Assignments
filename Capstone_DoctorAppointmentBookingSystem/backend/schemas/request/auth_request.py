from datetime import date
from pydantic import BaseModel, EmailStr, Field, field_validator

from enums.gender import Gender
from enums.user_role import UserRole
from enums.specialization import Specialization
from validators.auth_validators import (
    validate_full_name,
    validate_phone_number,
    validate_password,
    validate_date_of_birth,
    validate_role_patient,
    validate_role_doctor,
    validate_consultation_fee,
    validate_clinic_address,
)


class PatientRegisterRequest(BaseModel):
    """Request schema for patient registration."""

    full_name: str = Field(..., min_length=2)
    email: EmailStr
    password: str = Field(..., min_length=8, max_length=12)
    phone_number: str
    role: UserRole = Field(default=UserRole.PATIENT)
    gender: Gender
    date_of_birth: date

    @field_validator("full_name")
    @classmethod
    def check_full_name(cls, value: str) -> str:
        return validate_full_name(value)

    @field_validator("phone_number")
    @classmethod
    def check_phone_number(cls, value: str) -> str:
        return validate_phone_number(value)

    @field_validator("password")
    @classmethod
    def check_password(cls, value: str) -> str:
        return validate_password(value)

    @field_validator("date_of_birth")
    @classmethod
    def check_date_of_birth(cls, value: date) -> date:
        return validate_date_of_birth(value)

    @field_validator("role")
    @classmethod
    def check_role(cls, value: UserRole) -> UserRole:
        return validate_role_patient(value)


class DoctorRegisterRequest(BaseModel):
    """Request schema for doctor registration."""

    full_name: str = Field(..., min_length=2)
    email: EmailStr
    password: str = Field(..., min_length=8, max_length=12)
    phone_number: str
    role: UserRole = Field(default=UserRole.DOCTOR)
    qualification: str
    specialization: Specialization
    experience_years: int = Field(..., ge=0)
    license_number: str
    consultation_fee: float = Field(..., ge=0)
    clinic_address: str = Field(..., min_length=2)

    @field_validator("full_name")
    @classmethod
    def check_full_name(cls, value: str) -> str:
        return validate_full_name(value)

    @field_validator("phone_number")
    @classmethod
    def check_phone_number(cls, value: str) -> str:
        return validate_phone_number(value)

    @field_validator("password")
    @classmethod
    def check_password(cls, value: str) -> str:
        return validate_password(value)

    @field_validator("role")
    @classmethod
    def check_role(cls, value: UserRole) -> UserRole:
        return validate_role_doctor(value)

    @field_validator("consultation_fee")
    @classmethod
    def check_consultation_fee(cls, value: float) -> float:
        return validate_consultation_fee(value)

    @field_validator("clinic_address")
    @classmethod
    def check_clinic_address(cls, value: str) -> str:
        return validate_clinic_address(value)


class LoginRequest(BaseModel):
    """Request schema for user login."""

    email: EmailStr
    password: str