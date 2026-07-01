import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from constants.settings import settings
from database.database import connect_to_database, close_database_connection
from exceptions.exception_handler import register_exception_handlers
from middleware.logging_middleware import register_logging_middleware

# Routers
from routers.auth_router import router as auth_router
from routers.user_router import router as user_router
from routers.admin_router import router as admin_router
from routers.slot_router import router as slot_router
from routers.doctor_router import router as doctor_router
from routers.appointment_router import router as appointment_router
from routers.payment_router import router as payment_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Manages startup and shutdown of the database connection."""
    logger.info(f"Starting {settings.APP_NAME} in '{settings.APP_ENV}' mode...")
    await connect_to_database()

    yield

    logger.info(f"Shutting down {settings.APP_NAME}...")
    await close_database_connection()


app = FastAPI(
    title=settings.APP_NAME,
    description="Monolithic Backend for Doctor Appointment Booking System.",
    version="1.0.0",
    lifespan=lifespan,
)

register_exception_handlers(app)
register_logging_middleware(app)

# Include all routers
app.include_router(auth_router)
app.include_router(user_router)
app.include_router(admin_router)
app.include_router(slot_router)
app.include_router(doctor_router)
app.include_router(appointment_router)
app.include_router(payment_router)


@app.get("/health")
async def health_check():
    """Service health check."""
    return {
        "status": "ok",
        "service": settings.APP_NAME,
        "environment": settings.APP_ENV,
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
