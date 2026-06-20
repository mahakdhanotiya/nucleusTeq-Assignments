import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from constants.settings import settings
from database.database import connect_to_database, close_database_connection

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info(f"Starting {settings.APP_NAME}...")

    await connect_to_database()

    yield

    logger.info(f"Shutting down {settings.APP_NAME}...")
    await close_database_connection()


app = FastAPI(
    title=settings.APP_NAME,
    description="Handles user registration, login and authentication.",
    version="0.1.0",
    lifespan=lifespan,
)


@app.get("/health")
async def health_check():
    return {
        "status": "ok",
        "service": settings.APP_NAME,
        "environment": settings.APP_ENV,
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "main:app",
        host="127.0.0.1",
        port=8001,
        reload=True
    )