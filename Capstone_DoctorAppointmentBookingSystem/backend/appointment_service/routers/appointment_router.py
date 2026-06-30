from datetime import date
from typing import Optional

from fastapi import APIRouter, Depends, status

from dependencies.auth_dependency import (
    CurrentUser,
    get_current_user,
    require_doctor,
    require_patient,
)
from enums.appointment_status import AppointmentStatus
from schemas.request.appointment_request import (
    BookAppointmentRequest,
    CancelAppointmentRequest,
    UpdateAppointmentStatusRequest,
)
from schemas.response.appointment_response import (
    AppointmentCardResponse,
    AppointmentResponse,
)
from services.appointment_service import (
    book_appointment,
    cancel_appointment,
    get_appointment_detail,
    get_doctor_appointments,
    get_patient_appointments,
    update_appointment_status,
)

router = APIRouter(prefix="/appointments", tags=["Appointments"])


@router.post(
    "",
    response_model=AppointmentResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Book an appointment",
)
async def book(
    request: BookAppointmentRequest,
    current_user: CurrentUser = Depends(require_patient),
) -> AppointmentResponse:
    """Books a new appointment for the authenticated patient."""
    return await book_appointment(request, current_user)


@router.patch(
    "/{appointment_id}/cancel",
    response_model=AppointmentResponse,
    status_code=status.HTTP_200_OK,
    summary="Cancel an appointment",
)
async def cancel(
    appointment_id: str,
    request: CancelAppointmentRequest,
    current_user: CurrentUser = Depends(require_patient),
) -> AppointmentResponse:
    """Cancels an existing appointment for the patient."""
    return await cancel_appointment(appointment_id, request, current_user)


@router.get(
    "/my",
    response_model=list[AppointmentCardResponse],
    status_code=status.HTTP_200_OK,
    summary="Patient appointment history",
)
async def patient_history(
    appt_status: Optional[AppointmentStatus] = None,
    current_user: CurrentUser = Depends(require_patient),
) -> list[AppointmentCardResponse]:
    """Returns the patient's appointment history."""
    return await get_patient_appointments(current_user, status=appt_status)


@router.get(
    "/doctor/my",
    response_model=list[AppointmentCardResponse],
    status_code=status.HTTP_200_OK,
    summary="Doctor appointment views",
)
async def doctor_appointments(
    view: str = "upcoming",
    appointment_date: Optional[date] = None,
    current_user: CurrentUser = Depends(require_doctor),
) -> list[AppointmentCardResponse]:
    """Returns appointments assigned to the authenticated doctor."""
    return await get_doctor_appointments(current_user, view=view, appointment_date=appointment_date)


@router.patch(
    "/{appointment_id}/status",
    response_model=AppointmentResponse,
    status_code=status.HTTP_200_OK,
    summary="Mark appointment COMPLETED or NO_SHOW",
)
async def update_status(
    appointment_id: str,
    request: UpdateAppointmentStatusRequest,
    current_user: CurrentUser = Depends(require_doctor),
) -> AppointmentResponse:
    """
    Allows the doctor to mark an appointment as COMPLETED or NO_SHOW.
    Only allowed after the appointment end time has passed.
    """
    return await update_appointment_status(appointment_id, request, current_user)


@router.get(
    "/{appointment_id}",
    response_model=AppointmentResponse,
    status_code=status.HTTP_200_OK,
    summary="Get appointment detail",
)
async def get_detail(
    appointment_id: str,
    current_user: CurrentUser = Depends(get_current_user),
) -> AppointmentResponse:
    """Returns detailed information for an appointment."""
    return await get_appointment_detail(appointment_id, current_user)