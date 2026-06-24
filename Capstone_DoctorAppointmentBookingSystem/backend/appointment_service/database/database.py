import logging

from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient

from constants.settings import settings

logger = logging.getLogger(__name__)

# Holds the single shared MongoDB client for the lifetime of the application.
# Set during startup, cleared on shutdown.
mongo_client: AsyncIOMotorClient | None = None


async def connect_to_database() -> None:
    """
    Opens the MongoDB connection and initialises Beanie ODM.

    Called once at application startup. All Beanie Document models must be
    registered here so MongoDB creates their collections and indexes.

    Milestone 1.1: document_models is empty — no models exist yet.
    Milestone 1.2 will add: [Slot]
    Milestone 1.3 will add: [Slot, Appointment, Payment]
    """
    global mongo_client

    logger.info("Connecting to MongoDB...")

    mongo_client = AsyncIOMotorClient(settings.MONGO_URI)
    database = mongo_client[settings.DATABASE_NAME]

    # Register all Beanie Document models here.
    # Each model added here causes Beanie to create the corresponding
    # MongoDB collection and apply the indexes defined in the model's Settings.
    document_models: list = []  # Models will be added in later milestones

    await init_beanie(database=database, document_models=document_models)

    logger.info(f"Connected to MongoDB database: '{settings.DATABASE_NAME}'")


async def close_database_connection() -> None:
    """
    Closes the MongoDB connection cleanly on application shutdown.
    Prevents resource leaks when the server is stopped.
    """
    global mongo_client

    if mongo_client is not None:
        mongo_client.close()
        logger.info("MongoDB connection closed.")