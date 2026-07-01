"""Script to create the initial admin account."""

import asyncio
import os
import sys
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), '..')))

# Load .env before importing settings-dependent modules
from dotenv import load_dotenv
load_dotenv()

from database.database import connect_to_database, close_database_connection
from models.user import User
from enums.user_role import UserRole
from utils.password import hash_password


def _get_required_env(key: str) -> str:
    """Gets a required environment variable."""
    value = os.environ.get(key)
    if not value:
        logger.error(f"Environment variable '{key}' is not set.")
        logger.error("Add it to your .env file and try again.")
        sys.exit(1)
    return value


async def seed_admin() -> None:
    await connect_to_database()

    email       = _get_required_env("ADMIN_SEED_EMAIL")
    password    = _get_required_env("ADMIN_SEED_PASSWORD")
    full_name   = _get_required_env("ADMIN_SEED_FULL_NAME")
    phone       = _get_required_env("ADMIN_SEED_PHONE")

    existing = await User.find_one(User.email == email)
    if existing is not None:
        logger.info(f"Admin already exists: {email}")
        await close_database_connection()
        return

    admin = User(
        full_name=full_name,
        email=email,
        password_hash=hash_password(password),
        phone_number=phone,
        role=UserRole.ADMIN,
    )
    await admin.insert()

    logger.info("Admin created successfully.")
    logger.info(f"Email: {email}")
    logger.info(f"Role: {admin.role.value}")
    logger.info(f"ID: {admin.id}")

    await close_database_connection()


if __name__ == "__main__":
    asyncio.run(seed_admin())