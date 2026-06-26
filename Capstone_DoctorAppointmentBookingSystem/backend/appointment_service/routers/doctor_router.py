# Public-facing doctor endpoints for patients (FR-5, FR-6).
# Accessible by any authenticated user.

from datetime import date
from typing import Optional

from fastapi import APIRouter, Depends, status

from dependencies.auth_dependency import CurrentUser, get_current_user
from schemas.response.doctor_response import DoctorDetailResponse, DoctorSearchResult
from services.doctor_search_service import get_doctor_detail, search_doctors_service

router = APIRouter(prefix="/doctors", tags=["Doctors"])


@router.get(
    "/search",
    response_model=list[DoctorSearchResult],
    status_code=status.HTTP_200_OK,
    summary="Search doctors by name and/or specialization (FR-5)",
)
async def search_doctors(
    name: Optional[str] = None,
    specialization: Optional[str] = None,
    current_user: CurrentUser = Depends(get_current_user),
) -> list[DoctorSearchResult]:
    """
    Returns doctors matching the given filters.
    Only APPROVED and active doctors are returned.
    Each result includes an available_slot_count from the local slots collection.
    Both parameters are optional and can be combined.
    """
    return await search_doctors_service(name=name, specialization=specialization)


@router.get(
    "/{user_id}",
    response_model=DoctorDetailResponse,
    status_code=status.HTTP_200_OK,
    summary="Get doctor profile and available slots (FR-6)",
)
async def get_doctor(
    user_id: str,
    slot_date: Optional[date] = None,
    current_user: CurrentUser = Depends(get_current_user),
) -> DoctorDetailResponse:
    """
    Returns the full doctor profile from User Service combined with
    available slots from the local slots collection.
    Optional slot_date filter narrows slots to a specific date.
    """
    return await get_doctor_detail(user_id, slot_date=slot_date)