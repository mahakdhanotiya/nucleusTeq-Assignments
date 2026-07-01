"""Script to create the initial admin account."""

import asyncio
import os
import sys
import logging
from constants.settings import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), '..')))

from database.database import connect_to_database, close_database_connection
from models.user import User
from enums.user_role import UserRole
from utils.password import hash_password



async def seed_admin() -> None:
    await connect_to_database()

    email = settings.ADMIN_SEED_EMAIL
    password = settings.ADMIN_SEED_PASSWORD
    full_name = settings.ADMIN_SEED_FULL_NAME
    phone = settings.ADMIN_SEED_PHONE

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