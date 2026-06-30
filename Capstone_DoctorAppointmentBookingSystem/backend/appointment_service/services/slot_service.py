import logging
from datetime import date, datetime, timezone
from constants.message_constants import SLOT_DELETED_SUCCESS
 
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
    """Builds a slot response."""
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
    """Validates the slot date."""
    if slot_date < date.today():
        raise PastSlotDateError()
 
 
def _validate_slot_times(start_time: str, end_time: str) -> None:
    """Validates the slot time range."""
    if end_time <= start_time:
        raise InvalidSlotTimeError()
 
 
async def _check_for_overlap(
    doctor_id: PydanticObjectId,
    slot_date: date,
    start_time: str,
    end_time: str,
    exclude_slot_id: PydanticObjectId | None = None,
) -> None:
    """Checks for overlapping slots."""
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
    """Creates a new availability slot."""
    doctor_id = PydanticObjectId(current_user.user_id)
 
    _validate_slot_date(request.slot_date)
 
    _validate_slot_times(request.start_time, request.end_time)
 
    await _check_for_overlap(
        doctor_id=doctor_id,
        slot_date=request.slot_date,
        start_time=request.start_time,
        end_time=request.end_time,
    )
    
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
    """Returns the authenticated doctor's slots."""
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
    """Returns available slots for a doctor."""
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
    """Updates a doctor's slot."""
    object_id = PydanticObjectId(slot_id)
    slot = await get_slot_by_id(object_id)
 
    if slot is None:
        raise SlotNotFoundException(slot_id)
 
    if str(slot.doctor_id) != current_user.user_id:
        raise SlotNotOwnedByDoctorError()
 
    if slot.status == SlotStatus.BOOKED:
        raise SlotNotAvailableError()
 
    final_date = request.slot_date if request.slot_date is not None else slot.date
    final_start = request.start_time if request.start_time is not None else slot.start_time
    final_end = request.end_time if request.end_time is not None else slot.end_time
 
    _validate_slot_date(final_date)
 
    _validate_slot_times(final_start, final_end)
 
    await _check_for_overlap(
        doctor_id=slot.doctor_id,
        slot_date=final_date,
        start_time=final_start,
        end_time=final_end,
        exclude_slot_id=object_id,
    )
 
    slot.date = final_date
    slot.start_time = final_start
    slot.end_time = final_end
    
    updated = await update_slot(slot)
    logger.info(f"Slot updated: slot_id={slot_id}, doctor={current_user.email}")
    return _to_slot_response(updated)
 
 
async def delete_slot_for_doctor(
    slot_id: str,
    current_user: CurrentUser,
) -> MessageResponse:
    """Deletes a doctor's availability slot."""
    object_id = PydanticObjectId(slot_id)
    slot = await get_slot_by_id(object_id)
 
    if slot is None:
        raise SlotNotFoundException(slot_id)
 
    if str(slot.doctor_id) != current_user.user_id:
        raise SlotNotOwnedByDoctorError()

    # cannot be deleted
    if slot.status == SlotStatus.BOOKED:
        raise SlotNotAvailableError()
 
    await delete_slot(slot)
    logger.info(f"Slot deleted: slot_id={slot_id}, doctor={current_user.email}")
    return MessageResponse(message=SLOT_DELETED_SUCCESS)