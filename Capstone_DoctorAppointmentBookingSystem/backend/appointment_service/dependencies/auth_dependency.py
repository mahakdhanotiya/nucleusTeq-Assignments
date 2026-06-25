# FastAPI dependency functions for authentication and role enforcement

import jwt
from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from exceptions.appointment_exceptions import InvalidTokenError, UnauthorizedError
from utils.jwt_handler import decode_access_token

# HTTPBearer extracts the Bearer token from the Authorization header.
# auto_error=False means we handle missing tokens ourselves with a
# cleaner error message than FastAPI's default.
bearer_scheme = HTTPBearer(auto_error=False)


class CurrentUser:
    """
    Holds the authenticated user's identity extracted from the JWT payload.

    Passed into route handlers via Depends(get_current_user) so routes
    know who is making the request without a database lookup.
    """

    def __init__(self, user_id: str, email: str, role: str):
        self.user_id = user_id
        self.email = email
        self.role = role


async def get_current_user(
    credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme),
) -> CurrentUser:
    """
    Validates the Bearer JWT and returns the authenticated user's identity.

    Raises InvalidTokenError (→ 401) if:
      - No Authorization header is present
      - The token is malformed or has an invalid signature
      - The token has expired

    Used as the base dependency for all protected routes.
    """
    if credentials is None:
        raise InvalidTokenError("Authorization header is missing.")

    token = credentials.credentials

    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise InvalidTokenError("Token has expired. Please log in again.")
    except jwt.InvalidTokenError:
        raise InvalidTokenError("Invalid token.")

    user_id = payload.get("sub")
    email = payload.get("email")
    role = payload.get("role")

    if not user_id or not role:
        raise InvalidTokenError("Token is missing required claims.")

    return CurrentUser(user_id=user_id, email=email, role=role)


async def require_doctor(
    current_user: CurrentUser = Depends(get_current_user),
) -> CurrentUser:
    """
    Ensures the authenticated user has the DOCTOR role.
    Raises UnauthorizedError (→ 403) otherwise.
    Used for slot management routes.
    """
    if current_user.role != "DOCTOR":
        raise UnauthorizedError(required_role="DOCTOR")
    return current_user


async def require_patient(
    current_user: CurrentUser = Depends(get_current_user),
) -> CurrentUser:
    """
    Ensures the authenticated user has the PATIENT role.
    Raises UnauthorizedError (→ 403) otherwise.
    Used for appointment booking and cancellation routes.
    """
    if current_user.role != "PATIENT":
        raise UnauthorizedError(required_role="PATIENT")
    return current_user


async def require_admin(
    current_user: CurrentUser = Depends(get_current_user),
) -> CurrentUser:
    """
    Ensures the authenticated user has the ADMIN role.
    Raises UnauthorizedError (→ 403) otherwise.
    Used for admin dashboard and reporting routes.
    """
    if current_user.role != "ADMIN":
        raise UnauthorizedError(required_role="ADMIN")
    return current_user