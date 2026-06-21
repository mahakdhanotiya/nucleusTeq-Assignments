import logging
from datetime import datetime, timezone

from models.user import User
from schemas.request.user_request import UpdateProfileRequest, ChangePasswordRequest
from schemas.response.user_response import UserProfileResponse, MessageResponse
from utils.password import hash_password, verify_password
from repositories.user_repository import update_user, deactivate_user
from exceptions.user_exceptions import IncorrectPasswordError

logger = logging.getLogger(__name__)


def _to_profile_response(user: User) -> UserProfileResponse:
    return UserProfileResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
        role=user.role,
        is_active=user.is_active,
        created_at=user.created_at,
        updated_at=user.updated_at,
    )


async def get_my_profile(user: User) -> UserProfileResponse:
    """Returns the current authenticated user's profile."""
    return _to_profile_response(user)


async def update_my_profile(user: User, request: UpdateProfileRequest) -> UserProfileResponse:
    """Updates editable fields on the current user's account."""
    if request.full_name is not None:
        user.full_name = request.full_name
    if request.phone_number is not None:
        user.phone_number = request.phone_number

    user.updated_at = datetime.now(timezone.utc)
    await update_user(user)

    logger.info(f"User updated profile: {user.email}")
    return _to_profile_response(user)


async def change_password(user: User, request: ChangePasswordRequest) -> MessageResponse:
    """Validates the old password and sets a new hashed password."""
    if not verify_password(request.old_password, user.password_hash):
        raise IncorrectPasswordError()

    user.password_hash = hash_password(request.new_password)
    user.updated_at = datetime.now(timezone.utc)
    await update_user(user)

    logger.info(f"User changed password: {user.email}")
    return MessageResponse(message="Password changed successfully.")


async def deactivate_my_account(user: User) -> MessageResponse:
    """Deactivates the current user's account."""
    await deactivate_user(user)
    logger.info(f"User deactivated account: {user.email}")
    return MessageResponse(message="Account deactivated successfully.")