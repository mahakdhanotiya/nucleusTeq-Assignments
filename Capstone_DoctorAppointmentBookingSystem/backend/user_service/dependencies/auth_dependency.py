import jwt
from beanie import PydanticObjectId
from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

from models.user import User
from utils.jwt_handler import decode_access_token
from repositories.user_repository import get_user_by_id
from exceptions.user_exceptions import InvalidTokenError
from exceptions.auth_exceptions import AccountDeactivatedError

bearer_scheme = HTTPBearer()


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> User:
    """Validates the JWT and returns the authenticated User document."""
    token = credentials.credentials

    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise InvalidTokenError("Token has expired. Please log in again.")
    except jwt.InvalidTokenError:
        raise InvalidTokenError("Invalid token.")

    user_id = payload.get("sub")
    if user_id is None:
        raise InvalidTokenError("Token is missing required claims.")

    user = await get_user_by_id(PydanticObjectId(user_id))
    if user is None:
        raise InvalidTokenError("User associated with this token no longer exists.")

    if not user.is_active:
        raise AccountDeactivatedError()

    return user