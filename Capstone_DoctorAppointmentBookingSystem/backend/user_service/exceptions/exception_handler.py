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
    InvalidTokenError,
    UnauthorizedError,
    UserNotFoundError,
    IncorrectPasswordError,
)
from constants.message_constants import (
    DUPLICATE_EMAIL_RESPONSE,
    DUPLICATE_LICENSE_RESPONSE,
    INVALID_CREDENTIALS_ERROR,
    ACCOUNT_DEACTIVATED_ERROR,
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
    """Registers global exception handlers on the FastAPI app."""

    @app.exception_handler(DuplicateEmailError)
    async def handle_duplicate_email(request: Request, exc: DuplicateEmailError):
        logger.warning(f"Registration attempt with duplicate email: {exc.email}")
        return _build_error_response(
            error_code="DUPLICATE_EMAIL",
            message=DUPLICATE_EMAIL_RESPONSE,
            status_code=status.HTTP_409_CONFLICT,
        )

    @app.exception_handler(DuplicateLicenseNumberError)
    async def handle_duplicate_license(request: Request, exc: DuplicateLicenseNumberError):
        logger.warning(f"Registration attempt with duplicate license number: {exc.license_number}")
        return _build_error_response(
            error_code="DUPLICATE_LICENSE_NUMBER",
            message=DUPLICATE_LICENSE_RESPONSE,
            status_code=status.HTTP_409_CONFLICT,
        )

    @app.exception_handler(InvalidCredentialsError)
    async def handle_invalid_credentials(request: Request, exc: InvalidCredentialsError):
        logger.warning("Failed login attempt: invalid credentials.")
        return _build_error_response(
            error_code="INVALID_CREDENTIALS",
            message=INVALID_CREDENTIALS_ERROR,
            status_code=status.HTTP_401_UNAUTHORIZED,
        )

    @app.exception_handler(AccountDeactivatedError)
    async def handle_account_deactivated(request: Request, exc: AccountDeactivatedError):
        logger.warning("Login attempt on a deactivated account.")
        return _build_error_response(
            error_code="ACCOUNT_DEACTIVATED",
            message=ACCOUNT_DEACTIVATED_ERROR,
            status_code=status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(DoctorPendingApprovalError)
    async def handle_doctor_pending(request: Request, exc: DoctorPendingApprovalError):
        logger.warning("Login attempt by a doctor with PENDING approval status.")
        return _build_error_response(
            error_code="DOCTOR_PENDING_APPROVAL",
            message=str(exc),
            status_code=status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(DoctorRejectedError)
    async def handle_doctor_rejected(request: Request, exc: DoctorRejectedError):
        logger.warning("Login attempt by a doctor with REJECTED approval status.")
        return _build_error_response(
            error_code="DOCTOR_REJECTED",
            message=str(exc),
            status_code=status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(InvalidTokenError)
    async def handle_invalid_token(request: Request, exc: InvalidTokenError):
        logger.warning(f"Invalid token on {request.url.path}: {exc}")
        return _build_error_response(
            error_code="INVALID_TOKEN",
            message=str(exc),
            status_code=status.HTTP_401_UNAUTHORIZED,
        )

    @app.exception_handler(UnauthorizedError)
    async def handle_unauthorized(request: Request, exc: UnauthorizedError):
        logger.warning(f"Unauthorized access attempt on {request.url.path}: requires {exc.required_role}")
        return _build_error_response(
            error_code="UNAUTHORIZED",
            message=str(exc),
            status_code=status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(UserNotFoundError)
    async def handle_user_not_found(request: Request, exc: UserNotFoundError):
        logger.warning(f"User not found on {request.url.path}")
        return _build_error_response(
            error_code="USER_NOT_FOUND",
            message=str(exc),
            status_code=status.HTTP_404_NOT_FOUND,
        )

    @app.exception_handler(IncorrectPasswordError)
    async def handle_incorrect_password(request: Request, exc: IncorrectPasswordError):
        logger.warning(f"Incorrect old password on {request.url.path}")
        return _build_error_response(
            error_code="INCORRECT_PASSWORD",
            message=str(exc),
            status_code=status.HTTP_400_BAD_REQUEST,
        )

    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(f"Unhandled exception on {request.url.path}: {exc}", exc_info=True)
        return _build_error_response(
            error_code="INTERNAL_SERVER_ERROR",
            message=INTERNAL_SERVER_ERROR_RESPONSE,
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        )