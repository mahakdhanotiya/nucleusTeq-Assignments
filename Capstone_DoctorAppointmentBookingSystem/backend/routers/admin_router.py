import logging
from fastapi import APIRouter, Depends, status

from models.user import User
from models.appointment import Appointment
from enums.approval_status import ApprovalStatus
from enums.appointment_status import AppointmentStatus
from schemas.response.user_response import AdminDoctorResponse, AdminDashboardUsersResponse, MessageResponse
from schemas.response.admin_response import AppointmentStatsResponse

from dependencies.role_dependency import require_admin
from dependencies.auth_dependency import require_admin as require_admin_dto, CurrentUser

from services.admin_service import (
    list_all_doctors,
    set_doctor_active_status,
    set_doctor_approval_status,
    get_user_dashboard_stats,
)

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/admin", tags=["Admin"])


@router.get(
    "/doctors",
    response_model=list[AdminDoctorResponse],
    status_code=status.HTTP_200_OK,
    summary="List all doctors",
)
async def get_all_doctors(
    current_user: User = Depends(require_admin),
) -> list[AdminDoctorResponse]:
    """Returns all doctor accounts."""
    return await list_all_doctors()


@router.patch(
    "/doctors/{user_id}/approve",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
    summary="Approve a doctor account",
)
async def approve_doctor(
    user_id: str,
    current_user: User = Depends(require_admin),
) -> MessageResponse:
    """Approves a doctor's registration."""
    return await set_doctor_approval_status(user_id, ApprovalStatus.APPROVED)


@router.patch(
    "/doctors/{user_id}/reject",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
    summary="Reject a doctor account",
)
async def reject_doctor(
    user_id: str,
    current_user: User = Depends(require_admin),
) -> MessageResponse:
    """Rejects a doctor's registration."""
    return await set_doctor_approval_status(user_id, ApprovalStatus.REJECTED)


@router.patch(
    "/doctors/{user_id}/activate",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
    summary="Activate a doctor account (is_active)",
)
async def activate_doctor(
    user_id: str,
    current_user: User = Depends(require_admin),
) -> MessageResponse:
    """Activates a doctor account."""
    return await set_doctor_active_status(user_id, is_active=True)


@router.patch(
    "/doctors/{user_id}/deactivate",
    response_model=MessageResponse,
    status_code=status.HTTP_200_OK,
    summary="Deactivate a doctor account (is_active)",
)
async def deactivate_doctor(
    user_id: str,
    current_user: User = Depends(require_admin),
) -> MessageResponse:
    """Deactivates a doctor account — they can no longer log in"""
    return await set_doctor_active_status(user_id, is_active=False)


@router.get(
    "/dashboard/users",
    response_model=AdminDashboardUsersResponse,
    status_code=status.HTTP_200_OK,
    summary="Admin dashboard user statistics",
)
async def get_dashboard_user_stats(
    current_user: User = Depends(require_admin),
) -> AdminDashboardUsersResponse:
    """Returns admin dashboard statistics."""
    return await get_user_dashboard_stats()


@router.get(
    "/dashboard/appointments",
    response_model=AppointmentStatsResponse,
    status_code=status.HTTP_200_OK,
    summary="Appointment statistics for Admin Dashboard",
)
async def get_appointment_stats(
    current_user: CurrentUser = Depends(require_admin_dto),
) -> AppointmentStatsResponse:
    """Returns appointment statistics for the admin dashboard."""
    total = await Appointment.count()
    confirmed = await Appointment.find(
        Appointment.status == AppointmentStatus.CONFIRMED
    ).count()
    completed = await Appointment.find(
        Appointment.status == AppointmentStatus.COMPLETED
    ).count()
    cancelled = await Appointment.find(
        Appointment.status == AppointmentStatus.CANCELLED
    ).count()
    no_show = await Appointment.find(
        Appointment.status == AppointmentStatus.NO_SHOW
    ).count()

    logger.info(f"Admin dashboard stats fetched by: {current_user.user_id}")

    appointment_stats_response = AppointmentStatsResponse(
        total_appointments=total,
        confirmed_appointments=confirmed,
        completed_appointments=completed,
        cancelled_appointments=cancelled,
        no_show_appointments=no_show,
    )
    return appointment_stats_response
