import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exceptions.custom_exceptions import (
    DuplicateEmailError,
    DuplicateLicenseNumberError,
    InvalidCredentialsError,
    AccountDeactivatedError,
    DoctorPendingApprovalError,
    DoctorRejectedError,
)

from constants.auth_constants import (
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
    @app.exception_handler(DuplicateEmailError)
    async def handle_duplicate_email(request: Request, exc: DuplicateEmailError):
        logger.warning(f"Registration failure: {exc}")
        return _build_error_response(
            "DUPLICATE_EMAIL",
            DUPLICATE_EMAIL_RESPONSE,
            status.HTTP_409_CONFLICT,
        )

    @app.exception_handler(DuplicateLicenseNumberError)
    async def handle_duplicate_license(request: Request, exc: DuplicateLicenseNumberError):
        logger.warning(f"Registration failure: {exc}")
        return _build_error_response(
            "DUPLICATE_LICENSE",
            DUPLICATE_LICENSE_RESPONSE,
            status.HTTP_409_CONFLICT,
        )

    @app.exception_handler(InvalidCredentialsError)
    async def handle_invalid_credentials(request: Request, exc: InvalidCredentialsError):
        logger.warning("Failed login attempt")
        return _build_error_response(
            "INVALID_CREDENTIALS",
            str(exc),
            status.HTTP_401_UNAUTHORIZED,
        )

    @app.exception_handler(AccountDeactivatedError)
    async def handle_deactivated_account(request: Request, exc: AccountDeactivatedError):
        return _build_error_response(
            "ACCOUNT_DEACTIVATED",
            str(exc),
            status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(DoctorPendingApprovalError)
    async def handle_doctor_pending(request: Request, exc: DoctorPendingApprovalError):
        return _build_error_response(
            "PENDING_APPROVAL",
            str(exc),
            status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(DoctorRejectedError)
    async def handle_doctor_rejected(request: Request, exc: DoctorRejectedError):
        return _build_error_response(
            "REGISTRATION_REJECTED",
            str(exc),
            status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(
            f"Unhandled exception on {request.url.path}: {exc}",
            exc_info=True,
        )
        return _build_error_response(
            "INTERNAL_SERVER_ERROR",
            INTERNAL_SERVER_ERROR_RESPONSE,
            status.HTTP_500_INTERNAL_SERVER_ERROR,
        )