from datetime import date

from beanie import PydanticObjectId

from models.slot import Slot
from datetime import datetime, timezone, date


async def create_slot(slot: Slot) -> Slot:
    """
    Persists a new Slot document to MongoDB.

    The unique compound index on (doctor_id, date, start_time) will
    raise a DuplicateKeyError if an identical slot already exists.
    slot_service.py catches that error and raises a clean SlotConflictError.
    """
    await slot.insert()
    return slot


async def get_slot_by_id(slot_id: PydanticObjectId) -> Slot | None:
    """
    Fetches a single slot by its MongoDB _id.
    Returns None if the slot does not exist.
    """
    return await Slot.get(slot_id)


async def get_slots_by_doctor(
    doctor_id: PydanticObjectId,
    slot_date: date | None = None,
    status: str | None = None,
) -> list[Slot]:
    """
    Fetches all slots belonging to a specific doctor.

    Optional filters:
      - slot_date : restrict to a specific calendar date
      - status    : restrict to AVAILABLE or BOOKED slots

    Used by:
      - Doctors managing their own availability (GET /slots/my)
      - Patients viewing a doctor's available slots (GET /slots/doctor/{id})
    """
    
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
    """
    Fetches only AVAILABLE slots for a specific doctor.
    Convenience wrapper used during the booking flow.
    and when patients view a doctor's profile.
    """
    return await get_slots_by_doctor(
        doctor_id=doctor_id,
        slot_date=slot_date,
        status="AVAILABLE",
    )


async def update_slot(slot: Slot) -> Slot:
    """
    Saves changes made to an existing Slot document.

    The service layer is responsible for checking that the slot
    is AVAILABLE before calling this — BOOKED slots must not be modified.
    """
    # Refresh the audit timestamp before saving the updated document.
    slot.updated_at = datetime.now(timezone.utc)
    await slot.save()
    return slot


async def delete_slot(slot: Slot) -> None:
    """
    Permanently removes a slot from the database.

    The service layer is responsible for checking that the slot
    is AVAILABLE before calling this — BOOKED slots must not be deleted.
    """
    # Physically removes the document from the slots collection.
    await slot.delete()


async def get_overlapping_slot(
    doctor_id: PydanticObjectId,
    slot_date: date,
    start_time: str,
    end_time: str,
    exclude_slot_id: PydanticObjectId | None = None,
) -> Slot | None:
    """
    Checks whether a doctor already has a slot that overlaps with the
    given time range on the given date.

    Used by slot_service.py before creating or updating a slot to prevent
    a doctor from having two appointments at the same time.

    Overlap logic:
      Slot A overlaps with Slot B if A starts before B ends AND A ends after B starts.
      In other words: new_start < existing_end AND new_end > existing_start.

    The `exclude_slot_id` parameter allows excluding the slot being updated
    (so a doctor can update a slot without it conflicting with itself).
    """
    filters = [
        Slot.doctor_id == doctor_id,
        Slot.date == slot_date,
        # Overlap condition: existing slot's start is before the new end
        # AND existing slot's end is after the new start
        {"start_time": {"$lt": end_time}},
        {"end_time": {"$gt": start_time}},
    ]

    if exclude_slot_id is not None:
        # Exclude the slot being updated from the overlap check
        filters.append({"_id": {"$ne": exclude_slot_id}})

    return await Slot.find(*filters).first_or_none()