from beanie import PydanticObjectId
from beanie.odm.operators.find.comparison import In

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


async def search_doctor_profiles(
    specialization: str | None = None,
    user_ids: list[PydanticObjectId] | None = None,
) -> list[DoctorProfile]:
    """
    Searches doctor profiles by optional specialization filter and/or a list of user IDs.

    Used by GET /internal/doctors/search to support FR-5 doctor search.
    - If specialization is provided, filters case-insensitively.
    - If user_ids is provided, restricts results to that set of IDs.
    - Both filters can be combined.
    """
    query_filters = []

    if specialization:
        # Case-insensitive partial match on specialization
        query_filters.append(
            {"specialization": {"$regex": specialization, "$options": "i"}}
        )

    if user_ids is not None:
        query_filters.append(In(DoctorProfile.user_id, user_ids))

    if query_filters:
        return await DoctorProfile.find(*query_filters).to_list()

    return await DoctorProfile.find_all().to_list()