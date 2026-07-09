from constants.user_constants import INCORRECT_PASSWORD_ERROR, USER_NOT_FOUND_ERROR
from constants.doctor_constants import (
    DOCTOR_NOT_FOUND_TEMPLATE,
)

from constants.auth_constants import (
    ACCOUNT_DEACTIVATED_ERROR,
    INVALID_TOKEN_DEFAULT,
    UNAUTHORIZED_ROLE_TEMPLATE,
)


class AccountDeactivatedError(Exception):
    """Raised when a deactivated account attempts to log in."""

    def __init__(self):
        super().__init__(ACCOUNT_DEACTIVATED_ERROR)
class DoctorNotFoundError(Exception):
    """Raised when a doctor is not found."""

    def __init__(self, user_id: str = ""):
        self.user_id = user_id
        super().__init__(DOCTOR_NOT_FOUND_TEMPLATE.format(user_id))
        
class InvalidTokenError(Exception):
    """Raised when a JWT token is malformed, invalid, or expired."""

    def __init__(self, reason: str = INVALID_TOKEN_DEFAULT):
        super().__init__(reason)
        
class UnauthorizedError(Exception):
    """Raised when an authenticated user lacks the required role."""

    def __init__(self, required_role: str):
        self.required_role = required_role
        super().__init__(UNAUTHORIZED_ROLE_TEMPLATE.format(required_role))

class IncorrectPasswordError(Exception):
    """Raised when the old password provided during a password change is wrong."""

    def __init__(self):
        super().__init__(INCORRECT_PASSWORD_ERROR)

class UserNotFoundError(Exception):
    """Raised when a user referenced by ID cannot be found."""

    def __init__(self):
        super().__init__(USER_NOT_FOUND_ERROR)