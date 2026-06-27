# Custom exception classes for the Appointment Service.

class SlotNotFoundException(Exception):
    """Raised when a slot ID is provided but no matching slot exists."""

    def __init__(self, slot_id: str = ""):
        self.slot_id = slot_id
        super().__init__(f"Slot not found: {slot_id}")
        
class DoctorNotFoundError(Exception):
    """Raised when a doctor user_id does not exist in User Service."""
 
    def __init__(self, user_id: str = ""):
        self.user_id = user_id
        super().__init__(f"Doctor not found: {user_id}")


class SlotConflictError(Exception):
    """
    Raised when a doctor tries to create or update a slot that
    overlaps in time with one of their existing slots on the same date.
    """

    def __init__(self):
        super().__init__(
            "This time slot overlaps with an existing slot. "
            "Please choose a different time."
        )


class SlotNotAvailableError(Exception):
    """
    Raised when an operation requires a slot to be AVAILABLE
    but it is currently BOOKED.

    """

    def __init__(self):
        super().__init__(
            "This slot is already booked and cannot be modified or deleted."
        )


class SlotNotOwnedByDoctorError(Exception):
    """
    Raised when a doctor tries to modify or delete a slot
    that belongs to a different doctor.

    Prevents doctors from tampering with each other's schedules.
    """

    def __init__(self):
        super().__init__("You do not have permission to modify this slot.")


class InvalidSlotTimeError(Exception):
    """
    Raised when slot time values are logically invalid —
    specifically when end_time is not after start_time.
    """

    def __init__(self):
        super().__init__("End time must be after start time.")


class PastSlotDateError(Exception):
    """
    Raised when a doctor tries to create a slot on a past date.
    Slots must always be on today or a future date.
    """

    def __init__(self):
        super().__init__("Slot date must be today or a future date.")


# ---------------------------------------------------------------------------
# Auth exceptions (used by the dependency layer)
# ---------------------------------------------------------------------------


class InvalidTokenError(Exception):
    """Raised when the JWT token is missing, malformed, or expired."""

    def __init__(self, reason: str = "Invalid or expired token."):
        super().__init__(reason)


class UnauthorizedError(Exception):
    """Raised when an authenticated user's role is not permitted for an action."""

    def __init__(self, required_role: str):
        self.required_role = required_role
        super().__init__(f"This action requires the {required_role} role.")
        
        

# ---------------------------------------------------------------------------
# Appointment exceptions
# ---------------------------------------------------------------------------
 
class AppointmentNotFoundException(Exception):
    """Raised when an appointment ID does not exist."""
 
    def __init__(self, appointment_id: str = ""):
        self.appointment_id = appointment_id
        super().__init__(f"Appointment not found: {appointment_id}")
 
 
class AppointmentNotOwnedError(Exception):
    """Raised when a user tries to act on an appointment they do not own."""
 
    def __init__(self):
        super().__init__("You do not have permission to access this appointment.")
 
 
class PastAppointmentDateError(Exception):
    """Raised when a patient tries to book a slot on a past date (FR-7)."""
 
    def __init__(self):
        super().__init__("Appointment date must be today or a future date.")
 
 
class SlotAlreadyBookedError(Exception):
    """
    Raised when the requested slot is no longer AVAILABLE at booking time.
    Covers both the application-level check and the duplicate-key index catch.
    """
 
    def __init__(self):
        super().__init__(
            "This slot has already been booked. Please select another slot."
        )
 
 
class InvalidStatusTransitionError(Exception):
    """Raised when a status update violates the allowed state machine (FR-17)."""
 
    def __init__(self, current: str, requested: str):
        super().__init__(
            f"Cannot transition appointment from {current} to {requested}."
        )
 
 
class AppointmentNotCompletedYetError(Exception):
    """
    Raised when a doctor tries to mark an appointment COMPLETED or NO_SHOW
    before the appointment time has passed (FR-17).
    """
 
    def __init__(self):
        super().__init__(
            "Appointment status can only be updated after the appointment time has passed."
        )
 
 
class CancellationWindowExpiredError(Exception):
    """
    Raised when a patient tries to cancel within 2 hours of the appointment (FR-9).
    """
 
    def __init__(self):
        super().__init__(
            "Appointments can only be cancelled at least 2 hours before the scheduled time."
        )
 