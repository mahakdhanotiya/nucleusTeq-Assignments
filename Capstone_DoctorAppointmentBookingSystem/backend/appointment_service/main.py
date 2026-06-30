# Entry point for the Appointment Service.
#
#   - Create the FastAPI application
#   - Connect to MongoDB on startup, disconnect on shutdown
#   - Expose a health check endpoint at GET /health

import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from constants.settings import settings
from database.database import connect_to_database, close_database_connection
from exceptions.exception_handler import register_exception_handlers
from middleware.logging_middleware import register_logging_middleware
from routers.slot_router import router as slot_router
from routers.doctor_router import router as doctor_router
from routers.appointment_router import router as appointment_router
from routers.payment_router import router as payment_router
from routers.admin_router import router as admin_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Controls what happens when the application starts and stops.

    Everything before `yield` runs on startup.
    Everything after `yield` runs on shutdown.
    This is FastAPI's recommended pattern for lifecycle management.
    """
    # --- Startup ---
    logger.info(f"Starting {settings.APP_NAME} in '{settings.APP_ENV}' mode...")
    await connect_to_database()

    yield  # Application runs while paused here

    # --- Shutdown ---
    logger.info(f"Shutting down {settings.APP_NAME}...")
    await close_database_connection()


# Create the FastAPI application.
app = FastAPI(
    title=settings.APP_NAME,
    description=(
        "Manages doctor availability slots, appointment booking, "
        "mock payments, and appointment history."
    ),
    version="0.1.0",
    lifespan=lifespan,
)

# --- Cross-cutting concerns ---
register_exception_handlers(app)
register_logging_middleware(app)

# --- Routers ---
app.include_router(slot_router)          
app.include_router(doctor_router)        
app.include_router(appointment_router)  
app.include_router(payment_router) 
app.include_router(admin_router)  

# ---------------------------------------------------------------------------
# Health check
# ---------------------------------------------------------------------------

@app.get("/health", tags=["Health"])
async def health_check() -> dict:
    """
    Returns the current status of the Appointment Service.
    Use this to confirm the service started successfully and MongoDB is connected.
    """
    return {
        "status": "ok",
        "service": settings.APP_NAME,
        "environment": settings.APP_ENV,
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=8002, reload=True)