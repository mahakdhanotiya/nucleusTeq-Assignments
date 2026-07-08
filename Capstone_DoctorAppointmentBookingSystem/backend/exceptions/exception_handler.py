import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exceptions.custom_exceptions import (
    UserNotFoundError,
    IncorrectPasswordError,
    UnauthorizedError as UserUnauthorizedError,
    InvalidTokenError as UserInvalidTokenError,
)

from constants.common_constants import (
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

    # --- Generic Error ---
    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(f"Unhandled exception on {request.url.path}: {exc}", exc_info=True)
        return _build_error_response("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR_RESPONSE, status.HTTP_500_INTERNAL_SERVER_ERROR)
