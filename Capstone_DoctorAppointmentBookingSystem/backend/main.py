import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from middleware.cors_middleware import add_cors_middleware

from constants.settings import settings
from database.database import connect_to_database, close_database_connection
from exceptions.exception_handler import register_exception_handlers
from middleware.logging_middleware import register_logging_middleware
from middleware.cors_middleware import add_cors_middleware

# Routers

from routers.slot_router import router as slot_router

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
add_cors_middleware(app)

# Include all routers
app.include_router(slot_router)


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
