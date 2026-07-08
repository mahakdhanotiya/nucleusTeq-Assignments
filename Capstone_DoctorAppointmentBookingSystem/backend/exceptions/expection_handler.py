import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

   
from exceptions.custom_exceptions import DoctorNotFoundError


from constants.common_constants import INTERNAL_SERVER_ERROR_RESPONSE

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

    
    @app.exception_handler(DoctorNotFoundError)
    async def handle_doctor_not_found(request: Request, exc: DoctorNotFoundError):
        return _build_error_response("DOCTOR_NOT_FOUND", str(exc), status.HTTP_404_NOT_FOUND)


    # --- Generic Error ---
    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception):
        logger.error(f"Unhandled exception on {request.url.path}: {exc}", exc_info=True)
        return _build_error_response("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR_RESPONSE, status.HTTP_500_INTERNAL_SERVER_ERROR)
