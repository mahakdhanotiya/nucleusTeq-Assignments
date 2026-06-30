from datetime import datetime
from typing import Optional

from pydantic import BaseModel

from enums.user_role import UserRole
from enums.approval_status import ApprovalStatus


class UserProfileResponse(BaseModel):
    """User profile response model."""
    id: str
    full_name: str
    email: str
    phone_number: str
    role: UserRole
    is_active: bool
    approval_status: ApprovalStatus
    created_at: datetime
    updated_at: datetime


class MessageResponse(BaseModel):
    """Generic success response."""

    success: bool = True
    message: str


class DoctorProfileResponse(UserProfileResponse):
    """Doctor profile response model."""

    qualification: Optional[str] = None
    specialization: Optional[str] = None
    experience_years: Optional[int] = None
    license_number: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None
    profile_photo_url: Optional[str] = None


class AdminDoctorResponse(BaseModel):
    """Admin doctor response model."""
    user_id: str
    full_name: str
    email: str
    phone_number: str
    is_active: bool
    approval_status: ApprovalStatus
    specialization: Optional[str] = None
    qualification: Optional[str] = None
    experience_years: Optional[int] = None
    license_number: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None


class AdminDashboardUsersResponse(BaseModel):
    """User-side stats for the Admin Dashboard."""

    total_doctors: int
    total_patients: int
    active_doctors: int


class InternalDoctorResponse(BaseModel):
    """Internal doctor response model."""

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
    """Internal patient response model."""

    user_id: str
    full_name: str
    phone_number: str