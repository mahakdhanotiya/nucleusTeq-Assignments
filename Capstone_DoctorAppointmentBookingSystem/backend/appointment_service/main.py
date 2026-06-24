# Entry point for the Appointment Service.

import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from constants.settings import settings
from database.database import connect_to_database, close_database_connection

# Basic logging — a dedicated logging middleware will be added in Milestone 1.2
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
# Routers will be registered here incrementally as each milestone is completed.
app = FastAPI(
    title=settings.APP_NAME,
    description=(
        "Manages doctor availability slots, appointment booking, "
        "mock payments, and appointment history."
    ),
    version="0.1.0",
    lifespan=lifespan,
)

# Confirms the service is running and connected to MongoDB.

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


# Allows running directly with: python main.py
# Recommended command: uvicorn main:app --reload --port 8002
if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=8002, reload=True)