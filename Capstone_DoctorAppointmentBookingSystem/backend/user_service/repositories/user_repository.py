from models.user import User


async def get_user_by_email(email: str) -> User | None:
    """Fetches a user by email, or None if not found."""
    return await User.find_one(User.email == email)


async def create_user(user: User) -> User:
    """Persists a new User document."""
    await user.insert()
    return user