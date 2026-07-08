import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse


from exceptions.custom_exceptions import (
    SlotNotFoundException,
    SlotConflictError,
    SlotNotAvailableError,
    SlotNotOwnedByDoctorError,
    InvalidSlotTimeError,
    PastSlotDateError,
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


    @app.exception_handler(SlotNotFoundException)
    async def handle_slot_not_found(request: Request, exc: SlotNotFoundException):
        return _build_error_response("SLOT_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

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

    # --- Generic Error ---
    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(f"Unhandled exception on {request.url.path}: {exc}", exc_info=True)
        return _build_error_response("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR_RESPONSE, status.HTTP_500_INTERNAL_SERVER_ERROR)
