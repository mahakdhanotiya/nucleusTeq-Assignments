from enum import Enum


class AppointmentStatus(str, Enum):
    """Appointment status values."""

    CONFIRMED = "CONFIRMED"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"
    NO_SHOW = "NO_SHOW"