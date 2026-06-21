import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exceptions.auth_exceptions import (
    DuplicateEmailError,
    DuplicateLicenseNumberError,
    InvalidCredentialsError,
    AccountDeactivatedError,
)

logger = logging.getLogger(__name__)


def _build_error_response(error_code: str, message: str, status_code: int) -> JSONResponse:
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
            message="An account with this email already exists.",
            status_code=status.HTTP_409_CONFLICT,
        )

    @app.exception_handler(DuplicateLicenseNumberError)
    async def handle_duplicate_license(request: Request, exc: DuplicateLicenseNumberError):
        logger.warning(f"Registration attempt with duplicate license number: {exc.license_number}")
        return _build_error_response(
            error_code="DUPLICATE_LICENSE_NUMBER",
            message="A doctor account with this license number already exists.",
            status_code=status.HTTP_409_CONFLICT,
        )

    @app.exception_handler(InvalidCredentialsError)
    async def handle_invalid_credentials(request: Request, exc: InvalidCredentialsError):
        logger.warning("Failed login attempt: invalid credentials.")
        return _build_error_response(
            error_code="INVALID_CREDENTIALS",
            message="Invalid email or password.",
            status_code=status.HTTP_401_UNAUTHORIZED,
        )

    @app.exception_handler(AccountDeactivatedError)
    async def handle_account_deactivated(request: Request, exc: AccountDeactivatedError):
        logger.warning("Login attempt on a deactivated account.")
        return _build_error_response(
            error_code="ACCOUNT_DEACTIVATED",
            message="This account has been deactivated. Please contact support.",
            status_code=status.HTTP_403_FORBIDDEN,
        )

    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(f"Unhandled exception on {request.url.path}: {exc}", exc_info=True)
        return _build_error_response(
            error_code="INTERNAL_SERVER_ERROR",
            message="Something went wrong. Please try again later.",
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        )