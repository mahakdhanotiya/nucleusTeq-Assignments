from datetime import date

from beanie import PydanticObjectId

from models.slot import Slot
from datetime import datetime, timezone, date


async def create_slot(slot: Slot) -> Slot:
    """Creates and stores a new slot in the database."""
    await slot.insert()
    return slot


async def get_slot_by_id(slot_id: PydanticObjectId) -> Slot | None:
    """Returns a slot matching the specified identifier."""
    return await Slot.get(slot_id)


async def get_slots_by_doctor(
    doctor_id: PydanticObjectId,
    slot_date: date | None = None,
    status: str | None = None,
) -> list[Slot]:
    """Returns slots for a doctor using the provided filters."""
    
    # Build the query dynamically based on the filters provided by the caller.
    filters = [Slot.doctor_id == doctor_id]

    if slot_date is not None:
        filters.append(Slot.date == slot_date)

    if status is not None:
        filters.append(Slot.status == status)

    return await Slot.find(*filters).sort(
        # Always return slots in chronological order
        [("date", 1), ("start_time", 1)]
    ).to_list()


async def get_available_slots_for_doctor(
    doctor_id: PydanticObjectId,
    slot_date: date | None = None,
) -> list[Slot]:
    """Returns all available slots for the specified doctor."""
    return await get_slots_by_doctor(
        doctor_id=doctor_id,
        slot_date=slot_date,
        status="AVAILABLE",
    )


async def update_slot(slot: Slot) -> Slot:
    """Updates an existing slot and saves the latest changes."""
    slot.updated_at = datetime.now(timezone.utc)
    await slot.save()
    return slot


async def delete_slot(slot: Slot) -> None:
    """Deletes the specified slot from the database."""
    await slot.delete()


async def get_overlapping_slot(
    doctor_id: PydanticObjectId,
    slot_date: date,
    start_time: str,
    end_time: str,
    exclude_slot_id: PydanticObjectId | None = None,
) -> Slot | None:
    """Checks whether a conflicting slot already exists."""
    filters = [
        Slot.doctor_id == doctor_id,
        Slot.date == slot_date,

        {"start_time": {"$lt": end_time}},
        {"end_time": {"$gt": start_time}},
    ]

    if exclude_slot_id is not None:
        # Exclude the slot being updated from the overlap check
        filters.append({"_id": {"$ne": exclude_slot_id}})

    return await Slot.find(*filters).first_or_none()