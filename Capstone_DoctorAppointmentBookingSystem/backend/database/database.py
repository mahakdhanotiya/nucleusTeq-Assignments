import logging
import certifi

from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie

from constants.settings import settings

from models.user import User
from models.doctor_profile import DoctorProfile
from models.patient_profile import PatientProfile
from models.slot import Slot
from models.appointment import Appointment
from models.payment import Payment
from models.cancellation_request import DoctorCancellationRequest

logger = logging.getLogger(__name__)

mongo_client: AsyncIOMotorClient | None = None

async def connect_to_database() -> None:
    """Connects to MongoDB Atlas and initializes Beanie with all document models."""
    global mongo_client
    import os

    logger.info("Connecting to MongoDB...")

    mongo_client = AsyncIOMotorClient(
        settings.MONGO_URI,
        tls=True,
        tlsCAFile=certifi.where()
    )
    db_name = os.getenv("TEST_DB_NAME", "doctor_appointment_test_db") if os.getenv("TESTING") == "True" else settings.DATABASE_NAME
    database = mongo_client[db_name]

    document_models: list = [
        User, 
        DoctorProfile, 
        PatientProfile,
        Slot,
        Appointment,
        Payment,
        DoctorCancellationRequest
    ]

    await init_beanie(database=database, document_models=document_models)

    logger.info(f"Connected to MongoDB database: {settings.DATABASE_NAME}")

async def close_database_connection() -> None:
    """Closes the MongoDB connection."""
    global mongo_client

    if mongo_client is not None:
        mongo_client.close()
        logger.info("MongoDB connection closed.")