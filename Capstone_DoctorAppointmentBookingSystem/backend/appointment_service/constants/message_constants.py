# Validation and error messages for the Appointment Service

# Custom Exception Default Messages
SLOT_NOT_FOUND_TEMPLATE = "Slot not found: {}"
DOCTOR_NOT_FOUND_TEMPLATE = "Doctor not found: {}"
SLOT_CONFLICT_ERROR = "This time slot overlaps with an existing slot. Please choose a different time."
SLOT_NOT_AVAILABLE_ERROR = "This slot is already booked and cannot be modified or deleted."
SLOT_NOT_OWNED_BY_DOCTOR_ERROR = "You do not have permission to modify this slot."
END_TIME_MUST_BE_AFTER_START = "End time must be after start time."
SLOT_DATE_MUST_BE_TODAY_OR_FUTURE = "Slot date must be today or a future date."
INVALID_TOKEN_DEFAULT = "Invalid or expired token."
UNAUTHORIZED_ROLE_TEMPLATE = "This action requires the {} role."
APPOINTMENT_NOT_FOUND_TEMPLATE = "Appointment not found: {}"
APPOINTMENT_NOT_OWNED_ERROR = "You do not have permission to access this appointment."
APPOINTMENT_DATE_MUST_BE_TODAY_OR_FUTURE = "Appointment date must be today or a future date."
SLOT_ALREADY_BOOKED_ERROR = "This slot has already been booked. Please select another slot."
INVALID_STATUS_TRANSITION_TEMPLATE = "Cannot transition appointment from {} to {}."
APPOINTMENT_NOT_COMPLETED_YET_ERROR = (
    "Appointment status can only be updated after the appointment time has passed."
)
CANCELLATION_WINDOW_EXPIRED_ERROR = (
    "Appointments can only be cancelled at least 2 hours before the scheduled time."
)

# Exception Handler Error Responses
INTERNAL_SERVER_ERROR_RESPONSE = "Something went wrong. Please try again later."

# Token/Auth Dependency Messages
AUTH_HEADER_MISSING_ERROR = "Authorization header is missing."
TOKEN_EXPIRED_ERROR = "Token has expired. Please log in again."
INVALID_TOKEN_ERROR = "Invalid token."
TOKEN_MISSING_CLAIMS_ERROR = "Token is missing required claims."

# Schema Validation Messages
INVALID_TIME_FORMAT = "Time must be in HH:MM 24-hour format (e.g. '09:00', '17:30')."

# Service Return Success Messages
SLOT_DELETED_SUCCESS = "Slot deleted successfully."
