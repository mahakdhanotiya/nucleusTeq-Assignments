import re
from datetime import date
from typing import Optional
from enums.user_role import UserRole
from constants.auth_constants import (
    FULL_NAME_VALIDATION_ERROR,
    PHONE_NUMBER_VALIDATION_ERROR,
    PASSWORD_UPPERCASE_ERROR,
    PASSWORD_SPECIAL_CHAR_ERROR,
    PASSWORD_DIGIT_ERROR,
    DOB_MUST_BE_PAST_ERROR,
    ADMIN_REGISTRATION_FORBIDDEN_ERROR,
    CONSULTATION_FEE_VALIDATION_ERROR,
    CLINIC_ADDRESS_VALIDATION_ERROR,
)


def validate_full_name(value: str) -> str:
    """Validates that the full name contains only alphabets and spaces."""
    if not re.fullmatch(r"[A-Za-z\s]+", value):
        raise ValueError(FULL_NAME_VALIDATION_ERROR)
    return value.strip()


def validate_phone_number(value: str) -> str:
    """Validates that the phone number is exactly 10 digits."""
    if not re.fullmatch(r"\d{10}", value):
        raise ValueError(PHONE_NUMBER_VALIDATION_ERROR)
    return value


def validate_password(value: str) -> str:
    """Validates that the password meets complexity requirements."""
    if not re.search(r"[A-Z]", value):
        raise ValueError(PASSWORD_UPPERCASE_ERROR)

    if not re.search(r"\d", value):
        raise ValueError(PASSWORD_DIGIT_ERROR)

    if not re.search(r"[!@#$%^&*(),.?\":{}|<>_\-+=]", value):
        raise ValueError(PASSWORD_SPECIAL_CHAR_ERROR)

    return value


def validate_date_of_birth(value: Optional[date]) -> Optional[date]:
    """Validates that the date of birth is in the past."""
    if value is not None and value >= date.today():
        raise ValueError(DOB_MUST_BE_PAST_ERROR)
    return value


def validate_role_patient(value: UserRole) -> UserRole:
    """Validates that the role is PATIENT and not ADMIN."""
    if value == UserRole.ADMIN:
        raise ValueError(ADMIN_REGISTRATION_FORBIDDEN_ERROR)
    if value != UserRole.PATIENT:
        raise ValueError("Role must be PATIENT.")
    return value


def validate_role_doctor(value: UserRole) -> UserRole:
    """Validates that the role is DOCTOR and not ADMIN."""
    if value == UserRole.ADMIN:
        raise ValueError(ADMIN_REGISTRATION_FORBIDDEN_ERROR)
    if value != UserRole.DOCTOR:
        raise ValueError("Role must be DOCTOR.")
    return value


def validate_consultation_fee(value: float) -> float:
    """Validates that the consultation fee is a non-negative number."""
    if value < 0:
        raise ValueError(CONSULTATION_FEE_VALIDATION_ERROR)
    return value


def validate_clinic_address(value: str) -> str:
    """Validates that the clinic address is a non-empty string."""
    if not value or not value.strip():
        raise ValueError(CLINIC_ADDRESS_VALIDATION_ERROR)
    return value.strip()