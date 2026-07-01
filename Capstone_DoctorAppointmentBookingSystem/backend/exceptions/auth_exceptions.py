from constants.message_constants import (
    DUPLICATE_EMAIL_TEMPLATE,
    DUPLICATE_LICENSE_TEMPLATE,
    INVALID_CREDENTIALS_ERROR,
    ACCOUNT_DEACTIVATED_ERROR,
    DOCTOR_PENDING_APPROVAL_ERROR,
    DOCTOR_REJECTED_ERROR,
)


class DuplicateEmailError(Exception):
    """Raised when registering with an email that already exists."""

    def __init__(self, email: str):
        self.email = email
        super().__init__(DUPLICATE_EMAIL_TEMPLATE.format(email))


class DuplicateLicenseNumberError(Exception):
    """Raised when registering a doctor with a license number that already exists."""

    def __init__(self, license_number: str):
        self.license_number = license_number
        super().__init__(DUPLICATE_LICENSE_TEMPLATE.format(license_number))


class InvalidCredentialsError(Exception):
    """Raised when login fails due to a wrong email or password."""

    def __init__(self):
        super().__init__(INVALID_CREDENTIALS_ERROR)


class AccountDeactivatedError(Exception):
    """Raised when a deactivated account attempts to log in."""

    def __init__(self):
        super().__init__(ACCOUNT_DEACTIVATED_ERROR)
        

class DoctorPendingApprovalError(Exception):
    """Raised when a doctor's account is pending approval."""
 
    def __init__(self):
        super().__init__(DOCTOR_PENDING_APPROVAL_ERROR)
 
 
class DoctorRejectedError(Exception):
    """
    Raised when a doctor's account has been rejected.
    """
 
    def __init__(self):
        super().__init__(DOCTOR_REJECTED_ERROR)

 
 