import jwt
from beanie import PydanticObjectId
from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

from models.user import User
from utils.jwt_handler import decode_access_token
from repositories.user_repository import get_user_by_id
from exceptions.user_exceptions import InvalidTokenError as UserInvalidTokenError
from exceptions.auth_exceptions import AccountDeactivatedError
from exceptions.appointment_exceptions import InvalidTokenError as ApptInvalidTokenError, UnauthorizedError

from constants.message_constants import (
    AUTH_HEADER_MISSING_ERROR,
    TOKEN_EXPIRED_ERROR,
    INVALID_TOKEN_ERROR,
    TOKEN_MISSING_CLAIMS_ERROR,
    USER_NOT_FOUND_FOR_TOKEN_ERROR,
)

bearer_scheme = HTTPBearer(auto_error=False)

async def get_current_user(
    credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme),
) -> User:
    """Validates the JWT and returns the authenticated User document."""
    if credentials is None:
        raise UserInvalidTokenError(AUTH_HEADER_MISSING_ERROR)
    
    token = credentials.credentials
    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise UserInvalidTokenError(TOKEN_EXPIRED_ERROR)
    except jwt.InvalidTokenError:
        raise UserInvalidTokenError(INVALID_TOKEN_ERROR)

    user_id = payload.get("sub")
    if user_id is None:
        raise UserInvalidTokenError(TOKEN_MISSING_CLAIMS_ERROR)

    user = await get_user_by_id(PydanticObjectId(user_id))
    if user is None:
        raise UserInvalidTokenError(USER_NOT_FOUND_FOR_TOKEN_ERROR)

    if not user.is_active:
        raise AccountDeactivatedError()

    return user

class CurrentUser:
    """Represents the authenticated user extracted from the JWT (legacy compatibility)."""
    def __init__(self, user_id: str, email: str, role: str):
        self.user_id = user_id
        self.email = email
        self.role = role

async def get_current_user_dto(
    credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme),
) -> CurrentUser:
    """Validates the JWT and returns the CurrentUser DTO."""
    if credentials is None:
        raise ApptInvalidTokenError(AUTH_HEADER_MISSING_ERROR)

    token = credentials.credentials
    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise ApptInvalidTokenError(TOKEN_EXPIRED_ERROR)
    except jwt.InvalidTokenError:
        raise ApptInvalidTokenError(INVALID_TOKEN_ERROR)

    user_id = payload.get("sub")
    email = payload.get("email")
    role = payload.get("role")

    if not user_id or not role:
        raise ApptInvalidTokenError(TOKEN_MISSING_CLAIMS_ERROR)

    return CurrentUser(user_id=user_id, email=email, role=role)

async def require_doctor(current_user: CurrentUser = Depends(get_current_user_dto)) -> CurrentUser:
    if current_user.role != "DOCTOR":
        raise UnauthorizedError(required_role="DOCTOR")
    return current_user

async def require_patient(current_user: CurrentUser = Depends(get_current_user_dto)) -> CurrentUser:
    if current_user.role != "PATIENT":
        raise UnauthorizedError(required_role="PATIENT")
    return current_user

async def require_admin(current_user: CurrentUser = Depends(get_current_user_dto)) -> CurrentUser:
    if current_user.role != "ADMIN":
        raise UnauthorizedError(required_role="ADMIN")
    return current_user
