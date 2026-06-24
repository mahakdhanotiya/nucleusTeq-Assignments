# Defines the lifecycle states of a mock payment (FR-8).
# No real payment gateway is used — this simulates the payment flow.

from enum import Enum


class PaymentStatus(str, Enum):
    """
    Lifecycle states of a mock payment.

    PENDING — payment record created at booking time, awaiting confirmation.
    SUCCESS — patient confirmed payment via POST /payments/{id}/process.
    FAILED  — payment was explicitly failed (useful for testing edge cases).
    """

    PENDING = "PENDING"
    SUCCESS = "SUCCESS"
    FAILED = "FAILED"