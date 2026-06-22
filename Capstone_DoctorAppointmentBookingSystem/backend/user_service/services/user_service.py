import logging
from datetime import datetime, timezone

from models.user import User
from enums.user_role import UserRole
from schemas.request.user_request import UpdateProfileRequest, ChangePasswordRequest, UpdateDoctorProfileRequest
from schemas.response.user_response import UserProfileResponse, DoctorProfileResponse, MessageResponse
from utils.password import hash_password, verify_password
from repositories.user_repository import update_user
from repositories.doctor_repository import get_doctor_profile_by_user_id, update_doctor_profile
from exceptions.user_exceptions import IncorrectPasswordError

logger = logging.getLogger(__name__)


def _to_base_response(user: User) -> UserProfileResponse:
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


async def get_my_profile(user: User) -> DoctorProfileResponse | UserProfileResponse:
    """
    Returns the current authenticated user's profile.
    DOCTOR users receive the full DoctorProfileResponse including doctor-specific fields.
    All other roles receive the base UserProfileResponse.
    """
    if user.role == UserRole.DOCTOR:
        profile = await get_doctor_profile_by_user_id(user.id)
        return DoctorProfileResponse(
            id=str(user.id),
            full_name=user.full_name,
            email=user.email,
            phone_number=user.phone_number,
            role=user.role,
            is_active=user.is_active,
            created_at=user.created_at,
            updated_at=user.updated_at,
            qualification=profile.qualification if profile else None,
            specialization=profile.specialization if profile else None,
            experience_years=profile.experience_years if profile else None,
            license_number=profile.license_number if profile else None,
            consultation_fee=profile.consultation_fee if profile else None,
            clinic_address=profile.clinic_address if profile else None,
            profile_photo_url=profile.profile_photo_url if profile else None,
        )

    return _to_base_response(user)


async def update_my_profile(user: User, request: UpdateProfileRequest) -> DoctorProfileResponse | UserProfileResponse:
    """Updates common user fields (full_name, phone_number). Applicable to all roles."""
    if request.full_name is not None:
        user.full_name = request.full_name
    if request.phone_number is not None:
        user.phone_number = request.phone_number

    user.updated_at = datetime.now(timezone.utc)
    await update_user(user)

    logger.info(f"User updated profile: {user.email}")
    return await get_my_profile(user)


async def update_my_doctor_profile(
    user: User,
    request: UpdateDoctorProfileRequest,
) -> DoctorProfileResponse:
    """Updates doctor-specific profile fields (FR-16). Only callable by DOCTOR role users."""
    profile = await get_doctor_profile_by_user_id(user.id)

    if request.qualification is not None:
        profile.qualification = request.qualification
    if request.consultation_fee is not None:
        profile.consultation_fee = request.consultation_fee
    if request.clinic_address is not None:
        profile.clinic_address = request.clinic_address

    await update_doctor_profile(profile)

    logger.info(f"Doctor updated profile: {user.email}")
    return await get_my_profile(user)


async def change_password(user: User, request: ChangePasswordRequest) -> MessageResponse:
    """Validates the old password and sets a new hashed password."""
    if not verify_password(request.old_password, user.password_hash):
        raise IncorrectPasswordError()

    user.password_hash = hash_password(request.new_password)
    user.updated_at = datetime.now(timezone.utc)
    await update_user(user)

    logger.info(f"User changed password: {user.email}")
    return MessageResponse(message="Password changed successfully.")