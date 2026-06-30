from constants.message_constants import (
    INVALID_TOKEN_DEFAULT,
    UNAUTHORIZED_ROLE_TEMPLATE,
    USER_NOT_FOUND_ERROR,
    INCORRECT_PASSWORD_ERROR,
)


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