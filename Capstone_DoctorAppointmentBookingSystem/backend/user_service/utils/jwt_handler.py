from datetime import datetime, timedelta, timezone

import jwt

from constants.settings import settings


def create_access_token(
    user_id: str,
    email: str,
    role: str
) -> tuple[str, int]:
    """Generate a JWT access token."""

    now = datetime.now(timezone.utc)

    # Token expiry time
    expires_at = now + timedelta(
        minutes=settings.JWT_EXPIRY_MINUTES
    )

    # JWT payload data
    payload = {
        "sub": user_id,
        "email": email,
        "role": role,
        "iat": now,
        "exp": expires_at,
    }

    # Create signed JWT token
    token = jwt.encode(
        payload,
        settings.JWT_SECRET_KEY,
        algorithm=settings.JWT_ALGORITHM,
    )

    expires_in = settings.JWT_EXPIRY_MINUTES * 60

    return token, expires_in