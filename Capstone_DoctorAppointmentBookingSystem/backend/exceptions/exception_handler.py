import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exceptions.auth_exceptions import (
    DuplicateEmailError,
    DuplicateLicenseNumberError,
    InvalidCredentialsError,
    AccountDeactivatedError,
    DoctorPendingApprovalError,
    DoctorRejectedError,
)
from exceptions.user_exceptions import (
    UserNotFoundError,
    IncorrectPasswordError,
    UnauthorizedError as UserUnauthorizedError,
    InvalidTokenError as UserInvalidTokenError,
)
from exceptions.appointment_exceptions import (
    SlotNotFoundException,
    DoctorNotFoundError,
    SlotConflictError,
    SlotNotAvailableError,
    SlotNotOwnedByDoctorError,
    InvalidSlotTimeError,
    PastSlotDateError,
    InvalidTokenError as ApptInvalidTokenError,
    UnauthorizedError as ApptUnauthorizedError,
    AppointmentNotFoundException,
    AppointmentNotOwnedError,
    PastAppointmentDateError,
    SlotAlreadyBookedError,
    InvalidStatusTransitionError,
    AppointmentNotCompletedYetError,
    CancellationWindowExpiredError,
)

from constants.message_constants import (
    DUPLICATE_EMAIL_RESPONSE,
    DUPLICATE_LICENSE_RESPONSE,
    INTERNAL_SERVER_ERROR_RESPONSE,
)

logger = logging.getLogger(__name__)

def _build_error_response(error_code: str, message: str, status_code: int) -> JSONResponse:
    """Builds a standardized error response."""
    return JSONResponse(
        status_code=status_code,
        content={
            "success": False,
            "error_code": error_code,
            "message": message,
            "details": {},
        },
    )

def register_exception_handlers(app: FastAPI) -> None:
    """Registers all global exception handlers for the monolithic backend."""

    # --- User/Auth Service Exceptions ---
    @app.exception_handler(DuplicateEmailError)
    async def handle_duplicate_email(request: Request, exc: DuplicateEmailError):
        logger.warning(f"Registration failure: {exc}")
        return _build_error_response("DUPLICATE_EMAIL", DUPLICATE_EMAIL_RESPONSE, status.HTTP_409_CONFLICT)

    @app.exception_handler(DuplicateLicenseNumberError)
    async def handle_duplicate_license(request: Request, exc: DuplicateLicenseNumberError):
        logger.warning(f"Registration failure: {exc}")
        return _build_error_response("DUPLICATE_LICENSE", DUPLICATE_LICENSE_RESPONSE, status.HTTP_409_CONFLICT)

    @app.exception_handler(InvalidCredentialsError)
    async def handle_invalid_credentials(request: Request, exc: InvalidCredentialsError):
        logger.warning("Failed login attempt")
        return _build_error_response("INVALID_CREDENTIALS", str(exc), status.HTTP_401_UNAUTHORIZED)

    @app.exception_handler(AccountDeactivatedError)
    async def handle_deactivated_account(request: Request, exc: AccountDeactivatedError):
        return _build_error_response("ACCOUNT_DEACTIVATED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(DoctorPendingApprovalError)
    async def handle_doctor_pending(request: Request, exc: DoctorPendingApprovalError):
        return _build_error_response("PENDING_APPROVAL", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(DoctorRejectedError)
    async def handle_doctor_rejected(request: Request, exc: DoctorRejectedError):
        return _build_error_response("REGISTRATION_REJECTED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(UserInvalidTokenError)
    async def handle_user_invalid_token(request: Request, exc: UserInvalidTokenError):
        logger.warning(f"Invalid token on {request.url.path}: {exc}")
        return _build_error_response("INVALID_TOKEN", str(exc), status.HTTP_401_UNAUTHORIZED)

    @app.exception_handler(UserUnauthorizedError)
    async def handle_user_unauthorized(request: Request, exc: UserUnauthorizedError):
        logger.warning(f"Unauthorized access attempt on {request.url.path}: requires {exc.required_role}")
        return _build_error_response("UNAUTHORIZED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(UserNotFoundError)
    async def handle_user_not_found(request: Request, exc: UserNotFoundError):
        return _build_error_response("USER_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(IncorrectPasswordError)
    async def handle_incorrect_password(request: Request, exc: IncorrectPasswordError):
        return _build_error_response("INCORRECT_PASSWORD", str(exc), status.HTTP_400_BAD_REQUEST)

    # --- Appointment Service Exceptions ---
    @app.exception_handler(ApptInvalidTokenError)
    async def handle_appt_invalid_token(request: Request, exc: ApptInvalidTokenError):
        logger.warning(f"Auth failure on {request.url.path}: {exc}")
        return _build_error_response("INVALID_TOKEN", str(exc), status.HTTP_401_UNAUTHORIZED)

    @app.exception_handler(ApptUnauthorizedError)
    async def handle_appt_unauthorized(request: Request, exc: ApptUnauthorizedError):
        logger.warning(f"Role check failed on {request.url.path}: required={exc.required_role}")
        return _build_error_response("UNAUTHORIZED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(SlotNotFoundException)
    async def handle_slot_not_found(request: Request, exc: SlotNotFoundException):
        return _build_error_response("SLOT_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(DoctorNotFoundError)
    async def handle_doctor_not_found(request: Request, exc: DoctorNotFoundError):
        return _build_error_response("DOCTOR_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(SlotConflictError)
    async def handle_slot_conflict(request: Request, exc: SlotConflictError):
        return _build_error_response("SLOT_CONFLICT", str(exc), status.HTTP_409_CONFLICT)

    @app.exception_handler(SlotNotAvailableError)
    async def handle_slot_not_available(request: Request, exc: SlotNotAvailableError):
        return _build_error_response("SLOT_NOT_AVAILABLE", str(exc), status.HTTP_409_CONFLICT)

    @app.exception_handler(SlotNotOwnedByDoctorError)
    async def handle_slot_not_owned(request: Request, exc: SlotNotOwnedByDoctorError):
        return _build_error_response("SLOT_NOT_OWNED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(InvalidSlotTimeError)
    async def handle_invalid_time(request: Request, exc: InvalidSlotTimeError):
        return _build_error_response("INVALID_SLOT_TIME", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(PastSlotDateError)
    async def handle_past_date(request: Request, exc: PastSlotDateError):
        return _build_error_response("PAST_SLOT_DATE", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(AppointmentNotFoundException)
    async def handle_appointment_not_found(request: Request, exc: AppointmentNotFoundException):
        return _build_error_response("APPOINTMENT_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(AppointmentNotOwnedError)
    async def handle_appointment_not_owned(request: Request, exc: AppointmentNotOwnedError):
        return _build_error_response("APPOINTMENT_NOT_OWNED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(PastAppointmentDateError)
    async def handle_past_appointment_date(request: Request, exc: PastAppointmentDateError):
        return _build_error_response("PAST_APPOINTMENT_DATE", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(SlotAlreadyBookedError)
    async def handle_slot_already_booked(request: Request, exc: SlotAlreadyBookedError):
        return _build_error_response("SLOT_ALREADY_BOOKED", str(exc), status.HTTP_409_CONFLICT)

    @app.exception_handler(InvalidStatusTransitionError)
    async def handle_invalid_transition(request: Request, exc: InvalidStatusTransitionError):
        return _build_error_response("INVALID_STATUS_TRANSITION", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(AppointmentNotCompletedYetError)
    async def handle_not_completed_yet(request: Request, exc: AppointmentNotCompletedYetError):
        return _build_error_response("APPOINTMENT_NOT_COMPLETED_YET", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(CancellationWindowExpiredError)
    async def handle_cancellation_window(request: Request, exc: CancellationWindowExpiredError):
        return _build_error_response("CANCELLATION_WINDOW_EXPIRED", str(exc), status.HTTP_400_BAD_REQUEST)

    # --- Generic Error ---
    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(f"Unhandled exception on {request.url.path}: {exc}", exc_info=True)
        return _build_error_response("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR_RESPONSE, status.HTTP_500_INTERNAL_SERVER_ERROR)
