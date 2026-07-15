from enum import Enum


class RequestStatus(str, Enum):
    """Status options for bulk cancellation requests."""

    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
