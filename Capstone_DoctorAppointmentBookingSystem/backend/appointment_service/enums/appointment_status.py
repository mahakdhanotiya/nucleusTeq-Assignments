# Defines every valid state an appointment can be in.
# The allowed transitions between states are enforced in appointment_service.py.

from enum import Enum


class AppointmentStatus(str, Enum):
    """
    Lifecycle states of an appointment.

    Valid state transitions (all others are rejected):
        CONFIRMED → COMPLETED  : Doctor marks appointment as done (FR-17).
                                 Only allowed after appointment time has passed.
        CONFIRMED → CANCELLED  : Patient cancels (FR-9).
                                 Only allowed more than 2 hours before appointment.
        CONFIRMED → NO_SHOW    : Doctor marks patient as a no-show (FR-17).
                                 Only allowed after appointment time has passed.
    """

    CONFIRMED = "CONFIRMED"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"
    NO_SHOW = "NO_SHOW"