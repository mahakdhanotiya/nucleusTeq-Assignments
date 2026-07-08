from datetime import datetime, timedelta, timezone

import jwt

from constants.settings import settings


def create_access_token(user_id: str, email: str, role: str) -> tuple[str, int]:
    """Creates a signed JWT access token. Returns (token, expires_in_seconds)."""
    now = datetime.now(timezone.utc)
    expires_at = now + timedelta(seconds=settings.JWT_EXPIRY_SECONDS)

    payload = {
        "sub": user_id,
        "email": email,
        "role": role,
        "iat": now,
        "exp": expires_at,
    }

    token = jwt.encode(payload, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)

    return token, settings.JWT_EXPIRY_SECONDS


def decode_access_token(token: str) -> dict:
    """Decodes and verifies a JWT access token. Raises jwt exceptions on failure."""
    return jwt.decode(token, settings.JWT_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM])