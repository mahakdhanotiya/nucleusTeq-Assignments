from enum import Enum


class UserRole(str, Enum):
    """Roles supported by the system."""

    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"