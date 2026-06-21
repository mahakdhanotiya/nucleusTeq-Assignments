class DuplicateEmailError(Exception):
    """Raised when registering with an email that already exists."""

    def __init__(self, email: str):
        self.email = email
        super().__init__(f"Email already registered: {email}")


class DuplicateLicenseNumberError(Exception):
    """Raised when registering a doctor with a license number that already exists."""

    def __init__(self, license_number: str):
        self.license_number = license_number
        super().__init__(f"License number already registered: {license_number}")


class InvalidCredentialsError(Exception):
    """Raised when login fails due to a wrong email or password."""

    def __init__(self):
        super().__init__("Invalid email or password.")


class AccountDeactivatedError(Exception):
    """Raised when a deactivated account attempts to log in."""

    def __init__(self):
        super().__init__("This account has been deactivated. Please contact support.")