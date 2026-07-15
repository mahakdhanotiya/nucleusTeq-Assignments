from enum import Enum


class PaymentStatus(str, Enum):
    """Payment status values."""

    PENDING = "PENDING"
    SUCCESS = "SUCCESS"
    FAILED = "FAILED"