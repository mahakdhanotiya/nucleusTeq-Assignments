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
    summary="Search doctors by name and/or specialization",
)
async def search_doctors(
    name: Optional[str] = None,
    specialization: Optional[str] = None,
    current_user: CurrentUser = Depends(get_current_user),
) -> list[DoctorSearchResult]:
    """Returns doctors matching the specified search criteria."""
    return await search_doctors_service(name=name, specialization=specialization)


@router.get(
    "/{user_id}",
    response_model=DoctorDetailResponse,
    status_code=status.HTTP_200_OK,
    summary="Get doctor profile and available slots",
)
async def get_doctor(
    user_id: str,
    slot_date: Optional[date] = None,
    current_user: CurrentUser = Depends(get_current_user),
) -> DoctorDetailResponse:
    """Returns doctor details with available appointment slots."""
    return await get_doctor_detail(user_id, slot_date=slot_date)