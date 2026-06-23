from datetime import datetime
from typing import Optional

from pydantic import BaseModel

from enums.user_role import UserRole


class UserProfileResponse(BaseModel):
    """Response body for GET /users/me and PUT /users/me."""

    id: str
    full_name: str
    email: str
    phone_number: str
    role: UserRole
    is_active: bool
    created_at: datetime
    updated_at: datetime


class MessageResponse(BaseModel):
    """Generic confirmation response for actions with no resource to return."""

    success: bool = True
    message: str


class DoctorProfileResponse(UserProfileResponse):
    """
    Extended profile response for DOCTOR role users.
    Inherits all base user fields; adds doctor-specific fields.
    Patient users receive null for all doctor-specific fields.
    """

    qualification: Optional[str] = None
    specialization: Optional[str] = None
    experience_years: Optional[int] = None
    license_number: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None


class AdminDoctorResponse(BaseModel):
    """Single doctor entry in the admin doctor list."""

    user_id: str
    full_name: str
    email: str
    phone_number: str
    is_active: bool
    specialization: Optional[str] = None
    qualification: Optional[str] = None
    experience_years: Optional[int] = None
    license_number: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None


class AdminDashboardUsersResponse(BaseModel):
    """User-side stats for the Admin Dashboard (FR-20)."""

    total_doctors: int
    total_patients: int
    active_doctors: int


class InternalDoctorResponse(BaseModel):
    """
    Response contract for GET /internal/doctors/{user_id}.
    Consumed exclusively by Appointment Service for:
      - Doctor search result enrichment (FR-5)
      - Doctor detail page (FR-6)
      - Appointment booking snapshot (FR-7)

    Deliberately excludes: email, phone_number, license_number
    (User Service security concerns — Appointment Service has no need for them).
    """

    user_id: str
    full_name: str
    is_active: bool
    specialization: Optional[str] = None
    qualification: Optional[str] = None
    experience_years: Optional[int] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None


class InternalPatientResponse(BaseModel):
    """
    Response contract for GET /internal/patients/{user_id}.
    Consumed exclusively by Appointment Service at booking time (FR-7)
    to build the patient_snapshot stored inside the appointment document.

    Only name and phone are included — enough to satisfy FR-18 (doctor views
    patient info on an appointment) without exposing unnecessary patient data.
    """

    user_id: str
    full_name: str
    phone_number: str