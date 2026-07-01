from beanie import PydanticObjectId

from models.user import User
from enums.user_role import UserRole


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


async def get_all_doctors() -> list[User]:
    """Returns all users with the DOCTOR role."""
    return await User.find(User.role == UserRole.DOCTOR).to_list()


async def search_doctors_by_name(name: str) -> list[User]:
    """
    Searches active doctors by name.
    """
    return await User.find(
        User.role == UserRole.DOCTOR,
        User.is_active == True,
        {"full_name": {"$regex": name, "$options": "i"}},
    ).to_list()


async def get_user_counts() -> dict:
    """Returns user statistics for the admin dashboard."""
    total_doctors = await User.find(User.role == UserRole.DOCTOR).count()
    active_doctors = await User.find(
        User.role == UserRole.DOCTOR, User.is_active == True
    ).count()
    total_patients = await User.find(User.role == UserRole.PATIENT).count()

    return {
        "total_doctors": total_doctors,
        "total_patients": total_patients,
        "active_doctors": active_doctors,
    }