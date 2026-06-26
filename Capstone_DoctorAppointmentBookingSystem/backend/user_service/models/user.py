from datetime import datetime, timezone
from typing import Optional

from beanie import Document, Indexed
from pydantic import Field
from pymongo import IndexModel, ASCENDING

from enums.user_role import UserRole
from enums.approval_status import ApprovalStatus


class User(Document):
    """MongoDB document for authentication and common account data."""

    full_name: str = Field(..., min_length=2)
    email: Indexed(str, unique=True)
    password_hash: str
    phone_number: str = Field(..., min_length=10, max_length=10)
    role: UserRole
    is_active: bool = Field(default=True)

    # Only new DOCTOR registrations explicitly set this to PENDING
    # (handled in auth_service.py).
    approval_status: ApprovalStatus = Field(default=ApprovalStatus.APPROVED)

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "users"
        indexes = [
            IndexModel([("role", ASCENDING)], name="role_index"),
        ]