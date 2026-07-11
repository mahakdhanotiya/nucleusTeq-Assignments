"""Reusable validator functions for user request schemas."""

import re
from typing import Optional

from constants.common_constants import (
    FULL_NAME_VALIDATION_ERROR,
    PHONE_NUMBER_VALIDATION_ERROR,
    PASSWORD_UPPERCASE_ERROR,
    PASSWORD_SPECIAL_CHAR_ERROR,
)


def validate_full_name(value: Optional[str]) -> Optional[str]:
    """Validates that the full name contains only alphabets and spaces."""
    if value is not None and not re.fullmatch(r"[A-Za-z\s]+", value):
        raise ValueError(FULL_NAME_VALIDATION_ERROR)
    return value.strip() if value else value


def validate_phone_number(value: Optional[str]) -> Optional[str]:
    """Validates that the phone number is exactly 10 digits."""
    if value is not None and not re.fullmatch(r"\d{10}", value):
        raise ValueError(PHONE_NUMBER_VALIDATION_ERROR)
    return value


def validate_new_password(value: str) -> str:
    """Validates that the new password meets complexity requirements."""
    if not re.search(r"[A-Z]", value):
        raise ValueError(PASSWORD_UPPERCASE_ERROR)
    if not re.search(r"[!@#$%^&*(),.?\":{}|<>_\-+=]", value):
        raise ValueError(PASSWORD_SPECIAL_CHAR_ERROR)
    return value