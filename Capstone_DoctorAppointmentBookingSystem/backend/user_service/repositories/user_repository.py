from beanie import PydanticObjectId

from models.user import User


async def get_user_by_email(email: str) -> User | None:
    """Fetches a user by email, or None if not found."""
    return await User.find_one(User.email == email)


async def create_user(user: User) -> User:
    """Persists a new User document."""
    await user.insert()
    return user


async def get_user_by_id(user_id: PydanticObjectId) -> User | None:
    """Fetches a user by ID, or None if not found."""
    return await User.get(user_id)


async def update_user(user: User) -> User:
    """Saves changes made to an existing User document."""
    await user.save()
    return user


async def deactivate_user(user: User) -> User:
    """Marks a user account as inactive."""
    user.is_active = False
    await user.save()
    return user