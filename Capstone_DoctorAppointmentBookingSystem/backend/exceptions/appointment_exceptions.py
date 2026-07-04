from constants.message_constants import (
    APPOINTMENT_DATE_MUST_BE_TODAY_OR_FUTURE,
    APPOINTMENT_NOT_COMPLETED_YET_ERROR,
    APPOINTMENT_NOT_FOUND_TEMPLATE,
    APPOINTMENT_NOT_OWNED_ERROR,
    CANCELLATION_WINDOW_EXPIRED_ERROR,
    DOCTOR_NOT_FOUND_TEMPLATE,
    END_TIME_MUST_BE_AFTER_START,
    INVALID_STATUS_TRANSITION_TEMPLATE,
    INVALID_TOKEN_DEFAULT,
    SLOT_ALREADY_BOOKED_ERROR,
    SLOT_CONFLICT_ERROR,
    SLOT_DATE_MUST_BE_TODAY_OR_FUTURE,
    SLOT_NOT_AVAILABLE_ERROR,
    SLOT_NOT_FOUND_TEMPLATE,
    SLOT_NOT_OWNED_BY_DOCTOR_ERROR,
    UNAUTHORIZED_ROLE_TEMPLATE,
)


class SlotNotFoundException(Exception):
    """Raised when a slot is not found."""

    def __init__(self, slot_id: str = ""):
        self.slot_id = slot_id
        super().__init__(SLOT_NOT_FOUND_TEMPLATE.format(slot_id))


class DoctorNotFoundError(Exception):
    """Raised when a doctor is not found."""

    def __init__(self, user_id: str = ""):
        self.user_id = user_id
        super().__init__(DOCTOR_NOT_FOUND_TEMPLATE.format(user_id))


class SlotConflictError(Exception):
    """Raised when a slot overlaps with an existing slot."""

    def __init__(self):
        super().__init__(SLOT_CONFLICT_ERROR)


class SlotNotAvailableError(Exception):
    """Raised when a slot is not available."""

    def __init__(self):
        super().__init__(SLOT_NOT_AVAILABLE_ERROR)


class SlotNotOwnedByDoctorError(Exception):
    """Raised when a doctor accesses another doctor's slot."""

    def __init__(self):
        super().__init__(SLOT_NOT_OWNED_BY_DOCTOR_ERROR)


class InvalidSlotTimeError(Exception):
    """Raised when the slot time is invalid."""

    def __init__(self):
        super().__init__(END_TIME_MUST_BE_AFTER_START)


class PastSlotDateError(Exception):
    """Raised when the slot date is in the past."""

    def __init__(self):
        super().__init__(SLOT_DATE_MUST_BE_TODAY_OR_FUTURE)


class InvalidTokenError(Exception):
    """Raised when a JWT token is invalid."""

    def __init__(self, reason: str = INVALID_TOKEN_DEFAULT):
        super().__init__(reason)


class UnauthorizedError(Exception):
    """Raised when a user is not authorized."""

    def __init__(self, required_role: str):
        self.required_role = required_role
        super().__init__(UNAUTHORIZED_ROLE_TEMPLATE.format(required_role))


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