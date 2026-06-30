from datetime import datetime, timezone

from beanie import PydanticObjectId

from models.payment import Payment


async def create_payment(payment: Payment) -> Payment:
    """Persists a new Payment document."""
    await payment.insert()
    return payment


async def get_payment_by_appointment_id(appointment_id: PydanticObjectId) -> Payment | None:
    """Fetches the payment record linked to a given appointment."""
    return await Payment.find_one(Payment.appointment_id == appointment_id)


async def update_payment(payment: Payment) -> Payment:
    """Persists changes to an existing Payment document."""
    payment.updated_at = datetime.now(timezone.utc)
    await payment.save()
    return payment