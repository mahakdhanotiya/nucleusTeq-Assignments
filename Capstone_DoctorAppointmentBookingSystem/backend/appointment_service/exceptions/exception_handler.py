# Registers global exception handlers on the FastAPI app.

import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exceptions.appointment_exceptions import (
    SlotNotFoundException,
    DoctorNotFoundError,
    SlotConflictError,
    SlotNotAvailableError,
    SlotNotOwnedByDoctorError,
    InvalidSlotTimeError,
    PastSlotDateError,
    InvalidTokenError,
    UnauthorizedError,
    AppointmentNotFoundException,
    AppointmentNotOwnedError,
    PastAppointmentDateError,
    SlotAlreadyBookedError,
    InvalidStatusTransitionError,
    AppointmentNotCompletedYetError,
    CancellationWindowExpiredError,
)

logger = logging.getLogger(__name__)


def _error_response(error_code: str, message: str, status_code: int) -> JSONResponse:
    """
    Builds the standard error JSON shape used across all services:
        { success, error_code, message, details }
    """
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
    """
    Attaches all custom exception handlers to the FastAPI application.
    Once registered, any route or service that raises these exceptions
    will automatically receive the correct HTTP response — no try/except
    needed in routers or services.
    """

    @app.exception_handler(InvalidTokenError)
    async def handle_invalid_token(request: Request, exc: InvalidTokenError):
        logger.warning(f"Auth failure on {request.url.path}: {exc}")
        return _error_response("INVALID_TOKEN", str(exc), status.HTTP_401_UNAUTHORIZED)

    @app.exception_handler(UnauthorizedError)
    async def handle_unauthorized(request: Request, exc: UnauthorizedError):
        logger.warning(
            f"Role check failed on {request.url.path}: "
            f"required={exc.required_role}"
        )
        return _error_response("UNAUTHORIZED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(SlotNotFoundException)
    async def handle_slot_not_found(request: Request, exc: SlotNotFoundException):
        logger.warning(f"Slot not found on {request.url.path}: {exc.slot_id}")
        return _error_response("SLOT_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(DoctorNotFoundError)
    async def handle_doctor_not_found(request: Request, exc: DoctorNotFoundError):
        logger.warning(f"Doctor not found on {request.url.path}: {exc.user_id}")
        return _error_response("DOCTOR_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(SlotConflictError)
    async def handle_slot_conflict(request: Request, exc: SlotConflictError):
        logger.warning(f"Slot time conflict on {request.url.path}")
        return _error_response("SLOT_CONFLICT", str(exc), status.HTTP_409_CONFLICT)

    @app.exception_handler(SlotNotAvailableError)
    async def handle_slot_not_available(request: Request, exc: SlotNotAvailableError):
        logger.warning(f"Attempted to modify booked slot on {request.url.path}")
        return _error_response(
            "SLOT_NOT_AVAILABLE", str(exc), status.HTTP_409_CONFLICT
        )

    @app.exception_handler(SlotNotOwnedByDoctorError)
    async def handle_slot_not_owned(request: Request, exc: SlotNotOwnedByDoctorError):
        logger.warning(f"Slot ownership violation on {request.url.path}")
        return _error_response(
            "SLOT_NOT_OWNED", str(exc), status.HTTP_403_FORBIDDEN
        )

    @app.exception_handler(InvalidSlotTimeError)
    async def handle_invalid_time(request: Request, exc: InvalidSlotTimeError):
        return _error_response(
            "INVALID_SLOT_TIME", str(exc), status.HTTP_400_BAD_REQUEST
        )

    @app.exception_handler(PastSlotDateError)
    async def handle_past_date(request: Request, exc: PastSlotDateError):
        return _error_response(
            "PAST_SLOT_DATE", str(exc), status.HTTP_400_BAD_REQUEST
        )

    @app.exception_handler(AppointmentNotFoundException)
    async def handle_appointment_not_found(request: Request, exc: AppointmentNotFoundException):
        logger.warning(f"Appointment not found: {exc.appointment_id}")
        return _error_response("APPOINTMENT_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)

    @app.exception_handler(AppointmentNotOwnedError)
    async def handle_appointment_not_owned(request: Request, exc: AppointmentNotOwnedError):
        logger.warning(f"Appointment ownership violation on {request.url.path}")
        return _error_response("APPOINTMENT_NOT_OWNED", str(exc), status.HTTP_403_FORBIDDEN)

    @app.exception_handler(PastAppointmentDateError)
    async def handle_past_appointment_date(request: Request, exc: PastAppointmentDateError):
        return _error_response("PAST_APPOINTMENT_DATE", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(SlotAlreadyBookedError)
    async def handle_slot_already_booked(request: Request, exc: SlotAlreadyBookedError):
        logger.warning(f"Slot already booked on {request.url.path}")
        return _error_response("SLOT_ALREADY_BOOKED", str(exc), status.HTTP_409_CONFLICT)

    @app.exception_handler(InvalidStatusTransitionError)
    async def handle_invalid_transition(request: Request, exc: InvalidStatusTransitionError):
        return _error_response("INVALID_STATUS_TRANSITION", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(AppointmentNotCompletedYetError)
    async def handle_not_completed_yet(request: Request, exc: AppointmentNotCompletedYetError):
        return _error_response("APPOINTMENT_NOT_COMPLETED_YET", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(CancellationWindowExpiredError)
    async def handle_cancellation_window(request: Request, exc: CancellationWindowExpiredError):
        return _error_response("CANCELLATION_WINDOW_EXPIRED", str(exc), status.HTTP_400_BAD_REQUEST)

    @app.exception_handler(Exception)
    async def handle_unexpected(request: Request, exc: Exception):
        logger.error(
            f"Unhandled exception on {request.url.path}: {exc}", exc_info=True
        )
        return _error_response(
            "INTERNAL_SERVER_ERROR",
            "Something went wrong. Please try again later.",
            status.HTTP_500_INTERNAL_SERVER_ERROR,
        )