"""
scripts/seed_admin.py

One-time script to create the System Admin account.
Run once from inside the user_service directory:

    python scripts/seed_admin.py

Safe to run multiple times — will not create a duplicate if the admin
email already exists.

Credentials are read from environment variables (your .env file).
Never hardcode real credentials in this script.
"""

import asyncio
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# Load .env before importing settings-dependent modules
from dotenv import load_dotenv
load_dotenv()

from database.database import connect_to_database, close_database_connection
from models.user import User
from enums.user_role import UserRole
from utils.password import hash_password


def _get_required_env(key: str) -> str:
    """Reads a required env variable. Exits with a clear error if it is missing."""
    value = os.environ.get(key)
    if not value:
        print(f"ERROR: environment variable '{key}' is not set.")
        print("Add it to your .env file and try again.")
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
        print(f"Admin already exists: {email}")
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

    print("Admin created successfully.")
    print(f"  Email : {email}")
    print(f"  Role  : {admin.role.value}")
    print(f"  ID    : {admin.id}")

    await close_database_connection()


if __name__ == "__main__":
    asyncio.run(seed_admin())