from models.doctor_profile import DoctorProfile


async def get_doctor_by_license_number(license_number: str) -> DoctorProfile | None:
    """Fetches a doctor profile by license number, or None if not found."""
    return await DoctorProfile.find_one(DoctorProfile.license_number == license_number)


async def create_doctor_profile(profile: DoctorProfile) -> DoctorProfile:
    """Persists a new DoctorProfile document."""
    await profile.insert()
    return profile