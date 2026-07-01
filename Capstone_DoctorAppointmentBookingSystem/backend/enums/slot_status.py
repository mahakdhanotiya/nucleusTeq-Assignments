from enum import Enum


class SlotStatus(str, Enum):
    """Slot status values."""

    AVAILABLE = "AVAILABLE"
    BOOKED = "BOOKED"