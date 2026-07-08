import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exceptions.custom_exceptions import (
    AppointmentNotFoundException,
    AppointmentNotOwnedError,
    PastAppointmentDateError,
    SlotAlreadyBookedError,
    InvalidStatusTransitionError,
    AppointmentNotCompletedYetError,
    CancellationWindowExpiredError,
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

    
    # --- Appointment Service Exceptions ---

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
