from fastapi import APIRouter, Depends, status

from models.user import User
from schemas.response.user_response import AdminDoctorResponse, AdminDashboardUsersResponse, MessageResponse
from dependencies.role_dependency import require_admin
from services.admin_service import list_all_doctors, set_doctor_active_status, get_user_dashboard_stats

router = APIRouter(prefix="/admin", tags=["Admin"])


@router.get(
    "/doctors",
    response_model=list[AdminDoctorResponse],
    status_code=status.HTTP_200_OK,
)
async def get_all_doctors(current_user: User = Depends(require_admin)) -> list[AdminDoctorResponse]:
    """Returns all doctor accounts with their profile data (FR-19)."""
    return await list_all_doctors()


@router.patch(
    "/doctors/{user_id}/activate",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
)
async def activate_doctor(
    user_id: str,
    current_user: User = Depends(require_admin),
) -> MessageResponse:
    """Activates a doctor account (FR-19)."""
    return await set_doctor_active_status(user_id, is_active=True)


@router.patch(
    "/doctors/{user_id}/deactivate",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
)
async def deactivate_doctor(
    user_id: str,
    current_user: User = Depends(require_admin),
) -> MessageResponse:
    """Deactivates a doctor account (FR-19)."""
    return await set_doctor_active_status(user_id, is_active=False)


@router.get(
    "/dashboard/users",
    response_model=AdminDashboardUsersResponse,
    status_code=status.HTTP_200_OK,
)
async def get_dashboard_user_stats(
    current_user: User = Depends(require_admin),
) -> AdminDashboardUsersResponse:
    """Returns user-side stats for the Admin Dashboard: total_doctors, total_patients, active_doctors (FR-20)."""
    return await get_user_dashboard_stats()