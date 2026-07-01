from passlib.context import CryptContext

# BCrypt password hashing configuration
password_context = CryptContext(
    schemes=["bcrypt"],
    deprecated="auto"
)


def hash_password(plain_password: str) -> str:
    """Generate a hashed password."""
    return password_context.hash(plain_password)


def verify_password(
    plain_password: str,
    hashed_password: str
) -> bool:
    """Verify a password against its hash."""
    return password_context.verify(
        plain_password,
        hashed_password
    )