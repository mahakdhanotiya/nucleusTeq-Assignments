from constants.appointment_constants import (
    APPOINTMENT_DATE_MUST_BE_TODAY_OR_FUTURE,
    APPOINTMENT_NOT_COMPLETED_YET_ERROR,
    APPOINTMENT_NOT_FOUND_TEMPLATE,
    APPOINTMENT_NOT_OWNED_ERROR,
    CANCELLATION_WINDOW_EXPIRED_ERROR,
    INVALID_STATUS_TRANSITION_TEMPLATE,
    SLOT_ALREADY_BOOKED_ERROR,
    
)


class AppointmentNotFoundException(Exception):
    """Raised when an appointment is not found."""

    def __init__(self, appointment_id: str = ""):
        self.appointment_id = appointment_id
        super().__init__(APPOINTMENT_NOT_FOUND_TEMPLATE.format(appointment_id))


class AppointmentNotOwnedError(Exception):
    """Raised when a user accesses another user's appointment."""

    def __init__(self):
        super().__init__(APPOINTMENT_NOT_OWNED_ERROR)


class PastAppointmentDateError(Exception):
    """Raised when an appointment date is in the past."""

    def __init__(self):
        super().__init__(APPOINTMENT_DATE_MUST_BE_TODAY_OR_FUTURE)


class SlotAlreadyBookedError(Exception):
    """Raised when a slot has already been booked."""

    def __init__(self):
        super().__init__(SLOT_ALREADY_BOOKED_ERROR)


class InvalidStatusTransitionError(Exception):
    """Raised when an invalid appointment status transition is requested."""

    def __init__(self, current: str, requested: str):
        super().__init__(
            INVALID_STATUS_TRANSITION_TEMPLATE.format(current, requested)
        )


class AppointmentNotCompletedYetError(Exception):
    """Raised when an appointment has not been completed yet."""

    def __init__(self):
        super().__init__(APPOINTMENT_NOT_COMPLETED_YET_ERROR)


class CancellationWindowExpiredError(Exception):
    """Raised when the cancellation window has expired."""

    def __init__(self):
        super().__init__(CANCELLATION_WINDOW_EXPIRED_ERROR)