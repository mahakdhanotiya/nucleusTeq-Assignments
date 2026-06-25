# This layer sits between the router (HTTP) and the repository (database).
# The router calls these functions and returns whatever they produce.
# The router never applies business rules itself.

import logging
from datetime import date, datetime, timezone
 
from beanie import PydanticObjectId
 
from dependencies.auth_dependency import CurrentUser
from enums.slot_status import SlotStatus
from exceptions.appointment_exceptions import (
    InvalidSlotTimeError,
    PastSlotDateError,
    SlotConflictError,
    SlotNotFoundException,
    SlotNotAvailableError,
    SlotNotOwnedByDoctorError,
)
from models.slot import Slot
from repositories.slot_repository import (
    create_slot,
    delete_slot,
    get_available_slots_for_doctor,
    get_overlapping_slot,
    get_slot_by_id,
    get_slots_by_doctor,
    update_slot,
)
from schemas.request.slot_request import CreateSlotRequest, UpdateSlotRequest
from schemas.response.slot_response import MessageResponse, SlotResponse
 
logger = logging.getLogger(__name__)
 
 
def _to_slot_response(slot: Slot) -> SlotResponse:
    """Converts a Slot document into a SlotResponse schema."""
    return SlotResponse(
        id=str(slot.id),
        doctor_id=str(slot.doctor_id),
        date=slot.date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        status=slot.status,
        created_at=slot.created_at,
        updated_at=slot.updated_at,
    )
 
 
def _validate_slot_date(slot_date: date) -> None:
    """
    Raises PastSlotDateError if the given date is in the past.
    Slots must always be on today or a future date.
    """
    if slot_date < date.today():
        raise PastSlotDateError()
 
 
def _validate_slot_times(start_time: str, end_time: str) -> None:
    """
    Raises InvalidSlotTimeError if end_time is not strictly after start_time.
    This is a cross-field validation that cannot be done at the schema level
    when only one field is being updated.
    """
    if end_time <= start_time:
        raise InvalidSlotTimeError()
 
 
async def _check_for_overlap(
    doctor_id: PydanticObjectId,
    slot_date: date,
    start_time: str,
    end_time: str,
    exclude_slot_id: PydanticObjectId | None = None,
) -> None:
    """
    Raises SlotConflictError if the doctor already has a slot that
    overlaps with the given time range on the given date.
    """
    conflicting = await get_overlapping_slot(
        doctor_id=doctor_id,
        slot_date=slot_date,
        start_time=start_time,
        end_time=end_time,
        exclude_slot_id=exclude_slot_id,
    )
    if conflicting is not None:
        raise SlotConflictError()
 
 
async def create_slot_for_doctor(
    request: CreateSlotRequest,
    current_user: CurrentUser,
) -> SlotResponse:
    """
    Creates a new availability slot for the authenticated doctor (FR-14).
 
    Business rules applied:
      1. Slot date must be today or future
      2. End time must be after start time (also validated in schema)
      3. No overlap with existing slots for this doctor on this date
    """
    doctor_id = PydanticObjectId(current_user.user_id)
 
    # Rule 1: date must not be in the past
    _validate_slot_date(request.slot_date)
 
    # Rule 2: end must be after start (defence-in-depth, also checked in schema)
    _validate_slot_times(request.start_time, request.end_time)
 
    # Rule 3: no overlap with existing slots
    await _check_for_overlap(
        doctor_id=doctor_id,
        slot_date=request.slot_date,
        start_time=request.start_time,
        end_time=request.end_time,
    )
    
    # Create the new slot document after all business validations pass.
    slot = Slot(
        doctor_id=doctor_id,
        date=request.slot_date,
        start_time=request.start_time,
        end_time=request.end_time,
        status=SlotStatus.AVAILABLE,
    )
 
    created = await create_slot(slot)
    logger.info(
        f"Slot created: doctor={current_user.email}, "
        f"date={request.slot_date}, time={request.start_time}-{request.end_time}"
    )
    return _to_slot_response(created)
 
 
async def get_my_slots(
    current_user: CurrentUser,
    slot_date: date | None = None,
    status: str | None = None,
) -> list[SlotResponse]:
    """
    Returns all slots belonging to the authenticated doctor.
    Supports optional filters: date and/or status.
    Used by doctors managing their own availability schedule.
    """
    
    # Convert the authenticated doctor's ID into a MongoDB ObjectId.
    doctor_id = PydanticObjectId(current_user.user_id)
    slots = await get_slots_by_doctor(
        doctor_id=doctor_id,
        slot_date=slot_date,
        status=status,
    )
    return [_to_slot_response(s) for s in slots]
 
 
async def get_doctor_available_slots(
    doctor_id: str,
    slot_date: date | None = None,
) -> list[SlotResponse]:
    """
    Returns AVAILABLE slots for a specific doctor.
    Called by patients when viewing a doctor's profile to see
    what time slots are open for booking.
    """
    slots = await get_available_slots_for_doctor(
        doctor_id=PydanticObjectId(doctor_id),
        slot_date=slot_date,
    )
    return [_to_slot_response(s) for s in slots]
 
 
async def update_slot_for_doctor(
    slot_id: str,
    request: UpdateSlotRequest,
    current_user: CurrentUser,
) -> SlotResponse:
    """
    Updates a doctor's slot
 
    Business rules applied:
      1. Slot must exist
      2. Slot must belong to the requesting doctor
      3. Slot must be AVAILABLE — BOOKED slots cannot be modified
      4. If a new date is provided, it must be today or future
      5. Resolved end_time must be after resolved start_time
      6. Updated time window must not overlap with another existing slot
    """
    object_id = PydanticObjectId(slot_id)
    slot = await get_slot_by_id(object_id)
 
    # Rule 1: slot must exist
    if slot is None:
        raise SlotNotFoundException(slot_id)
 
    # Rule 2: ownership check
    if str(slot.doctor_id) != current_user.user_id:
        raise SlotNotOwnedByDoctorError()
 
    # Rule 3: must be AVAILABLE
    if slot.status == SlotStatus.BOOKED:
        raise SlotNotAvailableError()
 
    # Resolve the final values — use request value if provided, else keep existing
    final_date = request.slot_date if request.slot_date is not None else slot.date
    final_start = request.start_time if request.start_time is not None else slot.start_time
    final_end = request.end_time if request.end_time is not None else slot.end_time
 
    # Rule 4: date must not be in the past
    _validate_slot_date(final_date)
 
    # Rule 5: end must be after start (using resolved final values)
    _validate_slot_times(final_start, final_end)
 
    # Rule 6: no overlap (excluding this slot from the check)
    await _check_for_overlap(
        doctor_id=slot.doctor_id,
        slot_date=final_date,
        start_time=final_start,
        end_time=final_end,
        exclude_slot_id=object_id,
    )
 
    # Apply the changes
    slot.date = final_date
    slot.start_time = final_start
    slot.end_time = final_end
    
    # Persist the validated changes to MongoDB.
    updated = await update_slot(slot)
    logger.info(f"Slot updated: slot_id={slot_id}, doctor={current_user.email}")
    return _to_slot_response(updated)
 
 
async def delete_slot_for_doctor(
    slot_id: str,
    current_user: CurrentUser,
) -> MessageResponse:
    """
    Deletes a doctor's availability slot (FR-14).
 
    Business rules applied:
      1. Slot must exist
      2. Slot must belong to the requesting doctor
      3. Slot must be AVAILABLE — BOOKED slots cannot be deleted (FR-14)
    """
    object_id = PydanticObjectId(slot_id)
    slot = await get_slot_by_id(object_id)
 
    # Rule 1: must exist
    if slot is None:
        raise SlotNotFoundException(slot_id)
 
    # Rule 2: ownership check
    if str(slot.doctor_id) != current_user.user_id:
        raise SlotNotOwnedByDoctorError()
 
    # Rule 3: must be AVAILABLE — SRS FR-14 explicitly states booked slots
    # cannot be deleted
    if slot.status == SlotStatus.BOOKED:
        raise SlotNotAvailableError()
 
    await delete_slot(slot)
    logger.info(f"Slot deleted: slot_id={slot_id}, doctor={current_user.email}")
    return MessageResponse(message="Slot deleted successfully.")