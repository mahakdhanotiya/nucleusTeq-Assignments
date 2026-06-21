from fastapi import APIRouter, Depends, status

from models.user import User
from schemas.request.user_request import UpdateProfileRequest, ChangePasswordRequest
from schemas.response.user_response import UserProfileResponse, MessageResponse
from dependencies.auth_dependency import get_current_user
from services.user_service import (
    get_my_profile,
    update_my_profile,
    change_password,
    deactivate_my_account,
)

router = APIRouter(prefix="/users", tags=["User Management"])


@router.get("/me", response_model=UserProfileResponse, status_code=status.HTTP_200_OK)
async def get_profile(current_user: User = Depends(get_current_user)) -> UserProfileResponse:
    """Returns the current logged-in user's profile."""
    return await get_my_profile(current_user)


@router.put("/me", response_model=UserProfileResponse, status_code=status.HTTP_200_OK)
async def update_profile(
    request: UpdateProfileRequest,
    current_user: User = Depends(get_current_user),
) -> UserProfileResponse:
    """Updates the current user's profile."""
    return await update_my_profile(current_user, request)


@router.put("/change-password", response_model=MessageResponse, status_code=status.HTTP_200_OK)
async def update_password(
    request: ChangePasswordRequest,
    current_user: User = Depends(get_current_user),
) -> MessageResponse:
    """Changes the current user's password after validating the old one."""
    return await change_password(current_user, request)


@router.put("/deactivate", response_model=MessageResponse, status_code=status.HTTP_200_OK)
async def deactivate_account(current_user: User = Depends(get_current_user)) -> MessageResponse:
    """Deactivates the current user's account."""
    return await deactivate_my_account(current_user)