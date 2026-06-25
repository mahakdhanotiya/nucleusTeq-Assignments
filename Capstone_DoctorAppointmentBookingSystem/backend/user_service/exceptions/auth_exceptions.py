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
        

class DoctorPendingApprovalError(Exception):
    """
    Raised when a DOCTOR whose approval_status is PENDING attempts to log in.
    The doctor must wait for an Admin to approve their account before accessing the system.
    """
 
    def __init__(self):
        super().__init__(
            "Your account is pending admin approval. "
            "Please wait until your account is approved."
        )
 
 
class DoctorRejectedError(Exception):
    """
    Raised when a DOCTOR whose approval_status is REJECTED attempts to log in.
    """
 
    def __init__(self):
        super().__init__(
            "Your registration has been rejected. Please contact support."
        )
 
 