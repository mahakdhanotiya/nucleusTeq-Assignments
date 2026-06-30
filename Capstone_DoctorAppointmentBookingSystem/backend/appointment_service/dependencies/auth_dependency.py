import jwt
from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from exceptions.appointment_exceptions import InvalidTokenError, UnauthorizedError
from utils.jwt_handler import decode_access_token
from constants.message_constants import (
    AUTH_HEADER_MISSING_ERROR,
    TOKEN_EXPIRED_ERROR,
    INVALID_TOKEN_ERROR,
    TOKEN_MISSING_CLAIMS_ERROR,
)

bearer_scheme = HTTPBearer(auto_error=False)

class CurrentUser:
    """Represents the authenticated user extracted from the JWT."""

    def __init__(self, user_id: str, email: str, role: str):
        self.user_id = user_id
        self.email = email
        self.role = role


async def get_current_user(
    credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme),
) -> CurrentUser:
    """Validates the JWT and returns the authenticated user."""
    if credentials is None:
        raise InvalidTokenError(AUTH_HEADER_MISSING_ERROR)

    token = credentials.credentials

    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise InvalidTokenError(TOKEN_EXPIRED_ERROR)
    except jwt.InvalidTokenError:
        raise InvalidTokenError(INVALID_TOKEN_ERROR)

    user_id = payload.get("sub")
    email = payload.get("email")
    role = payload.get("role")

    if not user_id or not role:
        raise InvalidTokenError(TOKEN_MISSING_CLAIMS_ERROR)

    return CurrentUser(user_id=user_id, email=email, role=role)


async def require_doctor(
    current_user: CurrentUser = Depends(get_current_user),
) -> CurrentUser:
    """Ensures the current user has the DOCTOR role."""
    if current_user.role != "DOCTOR":
        raise UnauthorizedError(required_role="DOCTOR")
    return current_user


async def require_patient(
    current_user: CurrentUser = Depends(get_current_user),
) -> CurrentUser:
    """Ensures the current user has the PATIENT role."""
    if current_user.role != "PATIENT":
        raise UnauthorizedError(required_role="PATIENT")
    return current_user


async def require_admin(
    current_user: CurrentUser = Depends(get_current_user),
) -> CurrentUser:
    """Ensures the current user has the ADMIN role."""
    if current_user.role != "ADMIN":
        raise UnauthorizedError(required_role="ADMIN")
    return current_user