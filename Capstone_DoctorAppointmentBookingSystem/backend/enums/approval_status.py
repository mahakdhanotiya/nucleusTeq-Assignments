from enum import Enum


class ApprovalStatus(str, Enum):
    """Doctor approval status."""

    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"