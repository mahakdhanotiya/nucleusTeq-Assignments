# Field Validation Messages
FULL_NAME_VALIDATION_ERROR = "Full name must contain only alphabets and spaces."
PHONE_NUMBER_VALIDATION_ERROR = "Phone number must be exactly 10 digits."
PASSWORD_UPPERCASE_ERROR = "Password must contain at least one uppercase letter."
PASSWORD_SPECIAL_CHAR_ERROR = "Password must contain at least one special character."
PASSWORD_DIGIT_ERROR = "Password must contain at least one digit."
DOB_MUST_BE_PAST_ERROR = "Date of birth must be a past date."

# Registration Validation
PATIENT_REGISTRATION_MISSING_FIELDS_ERROR = (
    "Gender and date_of_birth are required for PATIENT registration."
)
DOCTOR_REGISTRATION_MISSING_FIELDS_TEMPLATE = (
    "Missing required DOCTOR fields: {}"
)
ADMIN_REGISTRATION_FORBIDDEN_ERROR = (
    "ADMIN accounts cannot be created through registration."
)

# Authentication Messages
DUPLICATE_EMAIL_TEMPLATE = "Email already registered: {}"
DUPLICATE_LICENSE_TEMPLATE = "License number already registered: {}"
INVALID_CREDENTIALS_ERROR = "Invalid email or password."
ACCOUNT_DEACTIVATED_ERROR = (
    "This account has been deactivated. Please contact support."
)
DOCTOR_PENDING_APPROVAL_ERROR = (
    "Your account is pending admin approval. Please wait until your account is approved."
)
DOCTOR_REJECTED_ERROR = (
    "Your registration has been rejected. Please contact support."
)

# JWT / Token
INVALID_TOKEN_DEFAULT = "Invalid or expired token."
UNAUTHORIZED_ROLE_TEMPLATE = "This action requires the {} role."

AUTH_HEADER_MISSING_ERROR = "Authorization header missing."
TOKEN_EXPIRED_ERROR = "Token has expired. Please log in again."
INVALID_TOKEN_ERROR = "Invalid token."
TOKEN_MISSING_CLAIMS_ERROR = "Token is missing required claims."
USER_NOT_FOUND_FOR_TOKEN_ERROR = (
    "User associated with this token no longer exists."
)
