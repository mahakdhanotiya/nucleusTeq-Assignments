from pydantic import BaseModel, Field, ConfigDict

from enums.user_role import UserRole
from constants.auth_constants import REGISTRATION_SUCCESS_MESSAGE


class UserSummaryResponse(BaseModel):
    """Minimal, safe user summary returned after login."""

    id: str
    full_name: str
    email: str
    role: UserRole

    model_config = ConfigDict(from_attributes=True)


class PatientRegisterResponse(BaseModel):
    """Response body for POST /auth/register/patient."""

    user_id: str
    email: str
    role: UserRole = UserRole.PATIENT
    message: str = REGISTRATION_SUCCESS_MESSAGE

    model_config = ConfigDict(from_attributes=True)


class DoctorRegisterResponse(BaseModel):
    """Response body for POST /auth/register/doctor."""

    user_id: str
    email: str
    role: UserRole = UserRole.DOCTOR
    message: str = REGISTRATION_SUCCESS_MESSAGE

    model_config = ConfigDict(from_attributes=True)


class TokenResponse(BaseModel):
    """Response body for POST /auth/login."""

    access_token: str
    token_type: str = "bearer"
    expires_in: int
    user: UserSummaryResponse

    model_config = ConfigDict(from_attributes=True)