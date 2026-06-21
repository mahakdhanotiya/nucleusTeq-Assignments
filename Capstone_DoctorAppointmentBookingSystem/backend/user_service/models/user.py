from datetime import date, datetime, timezone
from enum import Enum
from typing import Optional

from beanie import Document, Indexed
from pydantic import Field
from pymongo import IndexModel, ASCENDING

from enums.user_role import UserRole
from enums.gender import Gender


class User(Document):
    """
    User document storing authentication and basic profile details.
    """

    # Common fields for all users
    full_name: str = Field(
        ...,
        min_length=2,
        description="User's full name.",
    )

    email: Indexed(str, unique=True) = Field(
        ...,
        description="Unique email address.",
    )

    # Store hashed password only
    password_hash: str = Field(
        ...,
        description="BCrypt hashed password.",
    )

    phone_number: str = Field(
        ...,
        min_length=10,
        max_length=10,
        description="10-digit phone number.",
    )

    role: UserRole = Field(
        ...,
        description="User role.",
    )

    is_active: bool = Field(
        default=True,
        description="Account status.",
    )

    # Patient-specific fields
    gender: Optional[Gender] = Field(
        default=None,
        description="Applicable for patients.",
    )

    date_of_birth: Optional[date] = Field(
        default=None,
        description="Applicable for patients.",
    )

    # Audit fields
    created_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc),
    )

    updated_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc),
    )

    class Settings:
        """Beanie configuration."""

        name = "users"

        # Optimizes role-based queries
        indexes = [
            IndexModel([("role", ASCENDING)], name="role_index"),
        ]