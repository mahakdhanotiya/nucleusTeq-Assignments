# Defines the two possible states a time slot can be in.
# Used in the Slot model and enforced throughout the booking/cancellation flow.

from enum import Enum


class SlotStatus(str, Enum):
    """
    Lifecycle states of a doctor's availability slot.

    AVAILABLE — the slot exists and can be booked by a patient.
    BOOKED    — a patient has confirmed an appointment for this slot.
                The slot cannot be deleted or modified while in this state (FR-14).
                It returns to AVAILABLE if the appointment is cancelled (FR-9).
    """

    AVAILABLE = "AVAILABLE"
    BOOKED = "BOOKED"