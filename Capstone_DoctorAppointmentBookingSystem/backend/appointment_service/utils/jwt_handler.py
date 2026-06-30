# JWT token VERIFICATION for the Appointment Service.

import jwt

from constants.settings import settings


def decode_access_token(token: str) -> dict:
    """Decodes and verifies a JWT access token."""
    return jwt.decode(
        token,
        settings.JWT_SECRET_KEY,
        algorithms=[settings.JWT_ALGORITHM],
    )