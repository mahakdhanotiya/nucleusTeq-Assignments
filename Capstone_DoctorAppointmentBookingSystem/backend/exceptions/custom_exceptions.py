from constants.doctor_constants import DOCTOR_NOT_FOUND_TEMPLATE
from constants.user_constants import USER_NOT_FOUND_ERROR, INCORRECT_PASSWORD_ERROR
from constants.auth_constants import INVALID_TOKEN_DEFAULT, UNAUTHORIZED_ROLE_TEMPLATE, ACCOUNT_DEACTIVATED_ERROR
from constants.slot_constants import (
    SLOT_NOT_FOUND_TEMPLATE,
    SLOT_CONFLICT_ERROR,
    SLOT_NOT_AVAILABLE_ERROR,
    SLOT_NOT_OWNED_BY_DOCTOR_ERROR,
    END_TIME_MUST_BE_AFTER_START,
    SLOT_DATE_MUST_BE_TODAY_OR_FUTURE,
)
from constants.doctor_constants import DOCTOR_NOT_FOUND_ERROR

class InvalidTokenError(Exception):
    """Raised when a JWT token is malformed, invalid, or expired."""

    def __init__(self, reason: str = INVALID_TOKEN_DEFAULT):
        super().__init__(reason)

class UnauthorizedError(Exception):
    """Raised when an authenticated user lacks the required role."""

    def __init__(self, required_role: str):
        self.required_role = required_role
        super().__init__(UNAUTHORIZED_ROLE_TEMPLATE.format(required_role))
        
class UserNotFoundError(Exception):
    """Raised when a user referenced by ID cannot be found."""

    def __init__(self):
        super().__init__(USER_NOT_FOUND_ERROR)


class IncorrectPasswordError(Exception):
    """Raised when the old password provided during a password change is wrong."""

    def __init__(self):
        super().__init__(INCORRECT_PASSWORD_ERROR)
        
class DoctorNotFoundError(Exception):
    """Raised when a doctor is not found."""

    def __init__(self, user_id: str = ""):
        self.user_id = user_id
        super().__init__(DOCTOR_NOT_FOUND_TEMPLATE.format(user_id))


class SlotNotFoundException(Exception):
    """Raised when a slot is not found."""

    def __init__(self, slot_id: str = ""):
        self.slot_id = slot_id
        super().__init__(SLOT_NOT_FOUND_TEMPLATE.format(slot_id))


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
        
class AccountDeactivatedError(Exception):
    """Raised when a deactivated account attempts to log in."""

    def __init__(self):
        super().__init__(ACCOUNT_DEACTIVATED_ERROR)


