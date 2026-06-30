import logging
import certifi

from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient

from constants.settings import settings

logger = logging.getLogger(__name__)

mongo_client: AsyncIOMotorClient | None = None


async def connect_to_database() -> None:
    """Connects to MongoDB and initializes Beanie."""
    global mongo_client

    logger.info("Connecting to MongoDB...")

    mongo_client = AsyncIOMotorClient(
        settings.MONGO_URI,
        tls=True,
        tlsCAFile=certifi.where(),
    )
    database = mongo_client[settings.DATABASE_NAME]

    
    from models.slot import Slot
    from models.appointment import Appointment
    from models.payment import Payment

    document_models: list = [Slot, Appointment, Payment]

    await init_beanie(database=database, document_models=document_models)

    logger.info(f"Connected to MongoDB database: '{settings.DATABASE_NAME}'")


async def close_database_connection() -> None:
    """
    Closes the MongoDB connection.
    """
    global mongo_client

    if mongo_client is not None:
        mongo_client.close()
        logger.info("MongoDB connection closed.")