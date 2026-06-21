import logging

from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie

from constants.settings import settings
from models.user import User
from models.doctor_profile import DoctorProfile
from models.patient_profile import PatientProfile

logger = logging.getLogger(__name__)

mongo_client: AsyncIOMotorClient | None = None


async def connect_to_database() -> None:
    """Connects to MongoDB Atlas and initializes Beanie with all document models."""
    global mongo_client

    logger.info("Connecting to MongoDB...")

    import certifi

    mongo_client = AsyncIOMotorClient(
        settings.MONGO_URI,
        tlsCAFile=certifi.where()
    )
    database = mongo_client[settings.DATABASE_NAME]

    document_models: list = [User, DoctorProfile, PatientProfile]

    await init_beanie(database=database, document_models=document_models)

    logger.info(f"Connected to MongoDB database: {settings.DATABASE_NAME}")


async def close_database_connection() -> None:
    """Closes the MongoDB connection."""
    global mongo_client

    if mongo_client is not None:
        mongo_client.close()
        logger.info("MongoDB connection closed.")