import logging

from fastapi import APIRouter, Depends, status

from dependencies.auth_dependency import CurrentUser, require_admin
from enums.appointment_status import AppointmentStatus
from models.appointment import Appointment
from schemas.response.admin_response import AppointmentStatsResponse

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/admin", tags=["Admin"])


@router.get(
    "/dashboard/appointments",
    response_model=AppointmentStatsResponse,
    status_code=status.HTTP_200_OK,
    summary="Appointment statistics for Admin Dashboard",
)
async def get_appointment_stats(
    current_user: CurrentUser = Depends(require_admin),
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

    return AppointmentStatsResponse(
        total_appointments=total,
        confirmed_appointments=confirmed,
        completed_appointments=completed,
        cancelled_appointments=cancelled,
        no_show_appointments=no_show,
    )