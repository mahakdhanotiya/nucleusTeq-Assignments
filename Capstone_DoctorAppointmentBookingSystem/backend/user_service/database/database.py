import logging

import certifi
from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient

from constants.settings import settings

logger = logging.getLogger(__name__)

# Shared MongoDB client instance
mongo_client: AsyncIOMotorClient | None = None


async def connect_to_database() -> None:
    """Initialize MongoDB connection and Beanie."""

    global mongo_client

    logger.info("Connecting to MongoDB...")

    mongo_client = AsyncIOMotorClient(
        settings.MONGO_URI,
        tlsCAFile=certifi.where()
    )

    # Select application database
    database = mongo_client[settings.DATABASE_NAME]

    # Document models will be added here
    document_models: list = []

    await init_beanie(
        database=database,
        document_models=document_models
    )

    logger.info(
        f"Connected to MongoDB database: {settings.DATABASE_NAME}"
    )


async def close_database_connection() -> None:
    """Close MongoDB connection on application shutdown."""

    global mongo_client

    if mongo_client is not None:
        mongo_client.close()
        logger.info("MongoDB connection closed.")