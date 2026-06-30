import jwt
from beanie import PydanticObjectId
from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

from models.user import User
from utils.jwt_handler import decode_access_token
from repositories.user_repository import get_user_by_id
from exceptions.user_exceptions import InvalidTokenError
from exceptions.auth_exceptions import AccountDeactivatedError

from constants.message_constants import (
    TOKEN_EXPIRED_ERROR,
    INVALID_TOKEN_ERROR,
    TOKEN_MISSING_CLAIMS_ERROR,
    USER_NOT_FOUND_FOR_TOKEN_ERROR,
)

bearer_scheme = HTTPBearer()


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> User:
    """Validates the JWT and returns the authenticated User document."""
    token = credentials.credentials

    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise InvalidTokenError(TOKEN_EXPIRED_ERROR)
    except jwt.InvalidTokenError:
        raise InvalidTokenError(INVALID_TOKEN_ERROR)

    user_id = payload.get("sub")
    if user_id is None:
        raise InvalidTokenError(TOKEN_MISSING_CLAIMS_ERROR)

    user = await get_user_by_id(PydanticObjectId(user_id))
    if user is None:
        raise InvalidTokenError(USER_NOT_FOUND_FOR_TOKEN_ERROR)


    if not user.is_active:
        raise AccountDeactivatedError()

    return user