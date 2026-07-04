from models.patient_profile import PatientProfile


async def create_patient_profile(profile: PatientProfile) -> PatientProfile:
    """Persists a new PatientProfile document."""
    await profile.insert()
    return profile