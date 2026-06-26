import logging
from typing import Optional

from beanie import PydanticObjectId
from fastapi import APIRouter, Header, HTTPException, status

from constants.settings import settings
from enums.user_role import UserRole
from repositories.user_repository import get_user_by_id, search_doctors_by_name
from repositories.doctor_repository import (
    get_doctor_profile_by_user_id,
    search_doctor_profiles,
)
from schemas.response.user_response import InternalDoctorResponse, InternalPatientResponse

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/internal", tags=["Internal"])


def _verify_internal_key(x_internal_key: str) -> None:
    """Raises 403 if the provided key does not match the configured internal API key."""
    if x_internal_key != settings.INTERNAL_API_KEY:
        logger.warning("Internal endpoint called with invalid API key.")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Invalid internal API key.",
        )


def _parse_object_id(value: str) -> PydanticObjectId:
    """Parses a string into a PydanticObjectId, raising 422 on invalid format."""
    try:
        return PydanticObjectId(value)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Invalid ID format.",
        )


@router.get(
    "/doctors/search",
    response_model=list[InternalDoctorResponse],
    status_code=status.HTTP_200_OK,
    summary="Internal: Search doctors by name and/or specialization",
    description=(
        "Called by Appointment Service only. "
        "Requires X-Internal-Key header. "
        "Supports FR-5 doctor search by name and specialization. "
        "Returns only active doctors."
    ),
)
async def search_doctors_for_service(
    x_internal_key: str = Header(...),
    name: Optional[str] = None,
    specialization: Optional[str] = None,
) -> list[InternalDoctorResponse]:
    """
    Searches active doctors by name and/or specialization.

    FR-5: Patients shall search doctors by Doctor Name and Specialization.

    Strategy:
    - If name is provided: search users collection (owns full_name)
    - If specialization is provided: search doctor_profiles collection (owns specialization)
    - If both: intersect the two result sets
    - If neither: return all active doctors
    Both filters use case-insensitive partial matching.
    """
    _verify_internal_key(x_internal_key)

    # Step 1: Resolve matching user IDs from the users collection (for name filter)
    name_matched_ids: list[PydanticObjectId] | None = None
    if name:
        name_users = await search_doctors_by_name(name)
        name_matched_ids = [u.id for u in name_users]
        # If name was provided but matched nothing, return empty immediately
        if not name_matched_ids:
            return []

    # Step 2: Query doctor_profiles (for specialization filter, and to get profile data)
    profiles = await search_doctor_profiles(
        specialization=specialization,
        user_ids=name_matched_ids,  # None means "no user_id filter"
    )

    # Step 3: Compose response — fetch the User doc for each matching profile
    results: list[InternalDoctorResponse] = []
    for profile in profiles:
        user = await get_user_by_id(profile.user_id)
        # Skip inactive doctors — they should not appear in search results
        if user is None or not user.is_active:
            continue
        results.append(
            InternalDoctorResponse(
                user_id=str(user.id),
                full_name=user.full_name,
                is_active=user.is_active,
                specialization=profile.specialization,
                qualification=profile.qualification,
                experience_years=profile.experience_years,
                consultation_fee=profile.consultation_fee,
                clinic_address=profile.clinic_address,
                profile_photo_url=profile.profile_photo_url,
            )
        )

    logger.info(
        f"Internal: doctor search — name={name!r}, "
        f"specialization={specialization!r}, results={len(results)}"
    )
    return results


@router.get(
    "/doctors/{user_id}",
    response_model=InternalDoctorResponse,
    status_code=status.HTTP_200_OK,
    summary="Internal: Fetch doctor profile by user ID",
    description=(
        "Called by Appointment Service only. "
        "Requires X-Internal-Key header. "
        "Returns doctor profile for booking snapshot (FR-7) and detail view (FR-6)."
    ),
)
async def get_doctor_for_service(
    user_id: str,
    x_internal_key: str = Header(...),
) -> InternalDoctorResponse:
    """
    Returns one doctor's profile data for Appointment Service.
    Used at booking time to build doctor_snapshot and for FR-6 detail view.
    """
    _verify_internal_key(x_internal_key)

    object_id = _parse_object_id(user_id)
    user = await get_user_by_id(object_id)

    if user is None or user.role != UserRole.DOCTOR:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Doctor not found.",
        )

    profile = await get_doctor_profile_by_user_id(object_id)

    logger.info(f"Internal: doctor profile fetched for user_id={user_id}")

    return InternalDoctorResponse(
        user_id=str(user.id),
        full_name=user.full_name,
        is_active=user.is_active,
        specialization=profile.specialization if profile else None,
        qualification=profile.qualification if profile else None,
        experience_years=profile.experience_years if profile else None,
        consultation_fee=profile.consultation_fee if profile else None,
        clinic_address=profile.clinic_address if profile else None,
        profile_photo_url=profile.profile_photo_url if profile else None,
    )


@router.get(
    "/patients/{user_id}",
    response_model=InternalPatientResponse,
    status_code=status.HTTP_200_OK,
    summary="Internal: Fetch patient snapshot by user ID",
    description=(
        "Called by Appointment Service only at booking time (FR-7). "
        "Requires X-Internal-Key header. "
        "Returns patient name and phone for the patient_snapshot "
        "stored inside the appointment document."
    ),
)
async def get_patient_for_service(
    user_id: str,
    x_internal_key: str = Header(...),
) -> InternalPatientResponse:
    """
    Returns patient data for the booking snapshot (FR-7).
    The snapshot satisfies FR-18: doctor views patient name and phone on an appointment.
    Only name and phone are returned — email is deliberately excluded.
    """
    _verify_internal_key(x_internal_key)

    object_id = _parse_object_id(user_id)
    user = await get_user_by_id(object_id)

    if user is None or user.role != UserRole.PATIENT:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient not found.",
        )

    logger.info(f"Internal: patient snapshot fetched for user_id={user_id}")

    return InternalPatientResponse(
        user_id=str(user.id),
        full_name=user.full_name,
        phone_number=user.phone_number,
    )