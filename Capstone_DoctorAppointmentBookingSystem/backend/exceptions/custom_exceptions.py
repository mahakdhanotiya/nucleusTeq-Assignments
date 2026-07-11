
from constants.auth_constants import (
    DUPLICATE_EMAIL_TEMPLATE,
    DUPLICATE_LICENSE_TEMPLATE,
    INVALID_CREDENTIALS_ERROR,
    ACCOUNT_DEACTIVATED_ERROR,
    DOCTOR_PENDING_APPROVAL_ERROR,
    DOCTOR_REJECTED_ERROR,
    INVALID_TOKEN_DEFAULT,
    UNAUTHORIZED_ROLE_TEMPLATE
    
)

from constants.slot_constants import (
    SLOT_NOT_FOUND_TEMPLATE,
    SLOT_CONFLICT_ERROR,
    SLOT_NOT_AVAILABLE_ERROR,
    SLOT_NOT_OWNED_BY_DOCTOR_ERROR,
    END_TIME_MUST_BE_AFTER_START,
    SLOT_DATE_MUST_BE_TODAY_OR_FUTURE,
)

from constants.user_constants import (
    USER_NOT_FOUND_ERROR,
    INCORRECT_PASSWORD_ERROR
)


from constants.doctor_constants import (
    DOCTOR_NOT_FOUND_TEMPLATE,
)

from constants.appointment_constants import (
    APPOINTMENT_DATE_MUST_BE_TODAY_OR_FUTURE,
    APPOINTMENT_NOT_COMPLETED_YET_ERROR,
    APPOINTMENT_NOT_FOUND_TEMPLATE,
    APPOINTMENT_ACCESS_DENIED_ERROR,
    CANCELLATION_WINDOW_EXPIRED_ERROR,
    INVALID_STATUS_TRANSITION_TEMPLATE,
    SLOT_ALREADY_BOOKED_ERROR,
    
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


class InvalidTokenError(Exception):
    """Raised when a JWT token is malformed, invalid, or expired."""

    def __init__(self, reason: str = INVALID_TOKEN_DEFAULT):
        super().__init__(reason)


class DoctorNotFoundError(Exception):
    """Raised when a doctor is not found."""

    def __init__(self, user_id: str = ""):
        self.user_id = user_id
        super().__init__(DOCTOR_NOT_FOUND_TEMPLATE.format(user_id))
        
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
        
class AppointmentNotFoundException(Exception):
    """Raised when an appointment is not found."""

    def __init__(self, appointment_id: str = ""):
        self.appointment_id = appointment_id
        super().__init__(APPOINTMENT_NOT_FOUND_TEMPLATE.format(appointment_id))


class AppointmentAccessDeniedError(Exception):
    """Raised when a user attempts to access an appointment they are not assigned to."""

    def __init__(self):
        super().__init__(APPOINTMENT_ACCESS_DENIED_ERROR)


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


 
 