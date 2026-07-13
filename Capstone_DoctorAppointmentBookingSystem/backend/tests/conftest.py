import os
os.environ["TESTING"] = "True"
import asyncio
import pytest
import certifi
import pytest_asyncio
from typing import AsyncGenerator
from httpx import AsyncClient, ASGITransport
from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie

from main import app
from constants.settings import settings
from models.user import User
from models.doctor_profile import DoctorProfile
from models.patient_profile import PatientProfile
from models.slot import Slot
from models.appointment import Appointment
from models.payment import Payment
from models.cancellation_request import DoctorCancellationRequest

TEST_DB_NAME = f"doctor_appointment_test_db_{os.getpid()}"
os.environ["TEST_DB_NAME"] = TEST_DB_NAME

@pytest_asyncio.fixture(scope="session", autouse=True)
async def init_test_db():
    """Initialize Beanie on the test database once for the entire session."""
    client = AsyncIOMotorClient(
        settings.MONGO_URI,
        tls=True,
        tlsCAFile=certifi.where()
    )
    db = client[TEST_DB_NAME]
    
    document_models = [
        User, 
        DoctorProfile, 
        PatientProfile,
        Slot,
        Appointment,
        Payment,
        DoctorCancellationRequest
    ]
    
    await init_beanie(database=db, document_models=document_models)
    yield
    await client.drop_database(TEST_DB_NAME)
    client.close()

@pytest_asyncio.fixture(autouse=True)
async def clean_collections():
    """Clear MongoDB collections before each test run to ensure strict isolation."""
    await User.find_all().delete()
    await DoctorProfile.find_all().delete()
    await PatientProfile.find_all().delete()
    await Slot.find_all().delete()
    await Appointment.find_all().delete()
    await Payment.find_all().delete()
    await DoctorCancellationRequest.find_all().delete()

@pytest_asyncio.fixture(scope="session")
async def client() -> AsyncGenerator[AsyncClient, None]:
    """Provide a session-scoped async test client to prevent event loop recreation."""
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        yield ac

def pytest_collection_modifyitems(items):
    """Ensure all asyncio tests run in session loop scope to prevent loop recreation mismatch."""
    for item in items:
        for marker in item.iter_markers(name="asyncio"):
            marker.kwargs["loop_scope"] = "session"
