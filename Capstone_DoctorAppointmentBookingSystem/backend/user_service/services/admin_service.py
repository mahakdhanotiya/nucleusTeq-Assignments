import logging

from beanie import PydanticObjectId

from schemas.response.user_response import AdminDoctorResponse, AdminDashboardUsersResponse, MessageResponse
from repositories.user_repository import get_user_by_id, get_all_doctors, get_user_counts
from repositories.doctor_repository import get_doctor_profile_by_user_id
from exceptions.user_exceptions import UserNotFoundError, UnauthorizedError
from enums.user_role import UserRole

logger = logging.getLogger(__name__)


async def list_all_doctors() -> list[AdminDoctorResponse]:
    """Returns all doctor accounts with their profile data (FR-19)."""
    doctors = await get_all_doctors()
    result = []

    for user in doctors:
        profile = await get_doctor_profile_by_user_id(user.id)
        result.append(
            AdminDoctorResponse(
                user_id=str(user.id),
                full_name=user.full_name,
                email=user.email,
                phone_number=user.phone_number,
                is_active=user.is_active,
                specialization=profile.specialization if profile else None,
                qualification=profile.qualification if profile else None,
                experience_years=profile.experience_years if profile else None,
                license_number=profile.license_number if profile else None,
                consultation_fee=profile.consultation_fee if profile else None,
                clinic_address=profile.clinic_address if profile else None,
            )
        )

    return result


async def set_doctor_active_status(user_id: str, is_active: bool) -> MessageResponse:
    """
    Activates or deactivates a doctor account (FR-19).
    Raises UserNotFoundError if the user doesn't exist.
    Raises UnauthorizedError if the target user is not a DOCTOR.
    """
    user = await get_user_by_id(PydanticObjectId(user_id))

    if user is None:
        raise UserNotFoundError()

    if user.role != UserRole.DOCTOR:
        raise UnauthorizedError(required_role="DOCTOR")

    user.is_active = is_active
    await user.save()

    action = "activated" if is_active else "deactivated"
    logger.info(f"Admin {action} doctor account: {user.email}")

    return MessageResponse(message=f"Doctor account {action} successfully.")


async def get_user_dashboard_stats() -> AdminDashboardUsersResponse:
    """Returns user-side counts for the Admin Dashboard (FR-20)."""
    counts = await get_user_counts()
    return AdminDashboardUsersResponse(**counts)