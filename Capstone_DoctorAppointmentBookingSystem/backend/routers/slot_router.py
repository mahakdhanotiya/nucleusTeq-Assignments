# HTTP routes for doctor availability slot management

from datetime import date
from typing import Optional
 
from fastapi import APIRouter, Depends, status
 
from dependencies.auth_dependency import (
    CurrentUser,
    get_current_user,
    require_doctor,
)
from schemas.request.slot_request import CreateSlotRequest, UpdateSlotRequest
from schemas.response.slot_response import MessageResponse, SlotResponse
from services.slot_service import (
    create_slot_for_doctor,
    delete_slot_for_doctor,
    get_doctor_available_slots,
    get_my_slots,
    update_slot_for_doctor,
)
 
router = APIRouter(prefix="/slots", tags=["Slots"])
 
 
@router.post(
    "",
    response_model=SlotResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new availability slot",
)
async def create_slot(
    request: CreateSlotRequest,
    current_user: CurrentUser = Depends(require_doctor),
) -> SlotResponse:
    """Creates a new availability slot for the doctor."""
    return await create_slot_for_doctor(request, current_user)
 
 
@router.get(
    "/my",
    response_model=list[SlotResponse],
    status_code=status.HTTP_200_OK,
    summary="Get all my slots (Doctor)",
)
async def get_my_slots_route(
    slot_date: Optional[date] = None,
    slot_status: Optional[str] = None,
    current_user: CurrentUser = Depends(require_doctor),
) -> list[SlotResponse]:
    """Returns slots for the authenticated doctor."""
    return await get_my_slots(current_user, slot_date=slot_date, status=slot_status)
 
 
@router.get(
    "/doctor/{doctor_id}",
    response_model=list[SlotResponse],
    status_code=status.HTTP_200_OK,
    summary="Get available slots for a doctor (Patient view)",
)
async def get_available_slots(
    doctor_id: str,
    slot_date: Optional[date] = None,
    current_user: CurrentUser = Depends(get_current_user),
) -> list[SlotResponse]:
    """Returns available slots for the specified doctor."""
    return await get_doctor_available_slots(doctor_id, slot_date=slot_date)
 
 
@router.put(
    "/{slot_id}",
    response_model=SlotResponse,
    status_code=status.HTTP_200_OK,
    summary="Update a slot",
)
async def update_slot_route(
    slot_id: str,
    request: UpdateSlotRequest,
    current_user: CurrentUser = Depends(require_doctor),
) -> SlotResponse:
    """Updates an existing availability slot."""
    return await update_slot_for_doctor(slot_id, request, current_user)
 
 
@router.delete(
    "/{slot_id}",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
    summary="Delete a slot",
)
async def delete_slot_route(
    slot_id: str,
    current_user: CurrentUser = Depends(require_doctor),
) -> MessageResponse:
    """Deletes an availability slot."""
    return await delete_slot_for_doctor(slot_id, current_user)
 