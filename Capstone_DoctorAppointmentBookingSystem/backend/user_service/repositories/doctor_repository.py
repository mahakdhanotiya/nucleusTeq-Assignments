from beanie import PydanticObjectId

from models.doctor_profile import DoctorProfile


async def get_doctor_by_license_number(license_number: str) -> DoctorProfile | None:
    """Fetches a doctor profile by license number, or None if not found."""
    return await DoctorProfile.find_one(DoctorProfile.license_number == license_number)


async def create_doctor_profile(profile: DoctorProfile) -> DoctorProfile:
    """Persists a new DoctorProfile document."""
    await profile.insert()
    return profile


async def get_doctor_profile_by_user_id(user_id: PydanticObjectId) -> DoctorProfile | None:
    """Fetches a doctor's profile document by their user ID, or None if not found."""
    return await DoctorProfile.find_one(DoctorProfile.user_id == user_id)


async def update_doctor_profile(profile: DoctorProfile) -> DoctorProfile:
    """Saves changes made to an existing DoctorProfile document."""
    await profile.save()
    return profile