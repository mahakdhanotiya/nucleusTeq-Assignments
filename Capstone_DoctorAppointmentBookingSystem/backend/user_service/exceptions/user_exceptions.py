class InvalidTokenError(Exception):
    """Raised when a JWT token is malformed, invalid, or expired."""

    def __init__(self, reason: str = "Invalid or expired token."):
        super().__init__(reason)


class UnauthorizedError(Exception):
    """Raised when an authenticated user lacks the required role."""

    def __init__(self, required_role: str):
        self.required_role = required_role
        super().__init__(f"This action requires the {required_role} role.")


class UserNotFoundError(Exception):
    """Raised when a user referenced by ID cannot be found."""

    def __init__(self):
        super().__init__("User not found.")


class IncorrectPasswordError(Exception):
    """Raised when the old password provided during a password change is wrong."""

    def __init__(self):
        super().__init__("The current password you entered is incorrect.")