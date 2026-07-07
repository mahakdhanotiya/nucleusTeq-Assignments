from fastapi import APIRouter, Depends, status

from models.user import User
from schemas.request.user_request import UpdateProfileRequest, ChangePasswordRequest, UpdateDoctorProfileRequest
from schemas.response.user_response import DoctorProfileResponse, MessageResponse
from dependencies.auth_dependency import get_current_user
from dependencies.role_dependency import require_doctor
from services.user_service import (
    get_my_profile,
    update_my_profile,
    update_my_doctor_profile,
    change_password,
)

router = APIRouter(prefix="/users", tags=["User Management"])


@router.get("/me", response_model=DoctorProfileResponse, status_code=status.HTTP_200_OK)
async def get_profile(current_user: User = Depends(get_current_user)) -> DoctorProfileResponse:
    """
    Returns the authenticated user's profile.
    """
    return await get_my_profile(current_user)


@router.put("/me", response_model=DoctorProfileResponse, status_code=status.HTTP_200_OK)
async def update_profile(
    request: UpdateProfileRequest,
    current_user: User = Depends(get_current_user),
) -> DoctorProfileResponse:
    """Updates the authenticated user's profile."""
    return await update_my_profile(current_user, request)


@router.put(
    "/me/doctor-profile",
    response_model=DoctorProfileResponse,
    status_code=status.HTTP_200_OK,
)
async def update_doctor_profile(
    request: UpdateDoctorProfileRequest,
    current_user: User = Depends(require_doctor),
) -> DoctorProfileResponse:
    """
    Updates the authenticated doctor's profile.
    """
    return await update_my_doctor_profile(current_user, request)


@router.put("/change-password", response_model=MessageResponse, status_code=status.HTTP_200_OK)
async def update_password(
    request: ChangePasswordRequest,
    current_user: User = Depends(get_current_user),
) -> MessageResponse:
    """Changes the current user's password after validating the old one."""
    return await change_password(current_user, request)