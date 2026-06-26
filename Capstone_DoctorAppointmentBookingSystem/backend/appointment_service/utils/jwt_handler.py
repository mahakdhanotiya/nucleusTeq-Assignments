# JWT token VERIFICATION for the Appointment Service.

import jwt

from constants.settings import settings


def decode_access_token(token: str) -> dict:
    """
    Decodes and verifies a JWT access token.

    Returns the token payload (claims) as a dictionary if valid.
    Raises jwt.ExpiredSignatureError if the token has expired.
    Raises jwt.InvalidTokenError for any other issue (bad signature, malformed, etc.).

    The caller (auth_dependency.py) is responsible for catching these
    exceptions and converting them into the appropriate HTTP response.
    """
    return jwt.decode(
        token,
        settings.JWT_SECRET_KEY,
        algorithms=[settings.JWT_ALGORITHM],
    )