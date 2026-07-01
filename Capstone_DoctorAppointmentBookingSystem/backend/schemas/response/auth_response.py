from pydantic import BaseModel, Field

from enums.user_role import UserRole


class UserSummaryResponse(BaseModel):
    """Minimal, safe user summary returned after login."""

    id: str
    full_name: str
    email: str
    role: UserRole

    class Config:
        from_attributes = True


class RegisterResponse(BaseModel):
    """Response body for POST /auth/register."""

    user_id: str
    email: str
    role: UserRole
    message: str = "Registration successful. Please log in."


class TokenResponse(BaseModel):
    """Response body for POST /auth/login."""

    access_token: str
    token_type: str = "bearer"
    expires_in: int
    user: UserSummaryResponse