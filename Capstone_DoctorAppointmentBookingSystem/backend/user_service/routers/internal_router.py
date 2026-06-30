import logging
from typing import Optional

from beanie import PydanticObjectId
from fastapi import APIRouter, Header, HTTPException, status

from constants.settings import settings
from constants.message_constants import (
    INVALID_INTERNAL_API_KEY,
    INVALID_ID_FORMAT,
    DOCTOR_NOT_FOUND_ERROR,
    PATIENT_NOT_FOUND_ERROR,
)
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
    """
    Validates the internal API key.
    """
    if x_internal_key != settings.INTERNAL_API_KEY:
        logger.warning("Internal endpoint called with invalid API key.")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=INVALID_INTERNAL_API_KEY,
        )


def _parse_object_id(value: str) -> PydanticObjectId:
    """
    Converts a string to PydanticObjectId.
    """
    try:
        return PydanticObjectId(value)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=INVALID_ID_FORMAT,
        )


@router.get(
    "/doctors/search",
    response_model=list[InternalDoctorResponse],
    status_code=status.HTTP_200_OK,
    summary="Internal: Search doctors by name and/or specialization",
    description=(
        "Called by Appointment Service only. "
        "Requires X-Internal-Key header. "
        "Searches active doctors."
    ),
)
async def search_doctors_for_service(
    x_internal_key: str = Header(...),
    name: Optional[str] = None,
    specialization: Optional[str] = None,
) -> list[InternalDoctorResponse]:
    """
    Searches active doctors by name or specialization.
    """
    _verify_internal_key(x_internal_key)

    
    name_matched_ids: list[PydanticObjectId] | None = None
    if name:
        name_users = await search_doctors_by_name(name)
        name_matched_ids = [u.id for u in name_users]
        if not name_matched_ids:
            return []

    profiles = await search_doctor_profiles(
        specialization=specialization,
        user_ids=name_matched_ids,  # None means "no user_id filter"
    )

    results: list[InternalDoctorResponse] = []
    for profile in profiles:
        user = await get_user_by_id(profile.user_id)

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
        "Returns doctor profile for booking snapshot and detail view."
    ),
)
async def get_doctor_for_service(
    user_id: str,
    x_internal_key: str = Header(...),
) -> InternalDoctorResponse:
    """
    Returns a doctor's profile for internal service calls.
    """
    _verify_internal_key(x_internal_key)

    object_id = _parse_object_id(user_id)
    user = await get_user_by_id(object_id)

    if user is None or user.role != UserRole.DOCTOR:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=DOCTOR_NOT_FOUND_ERROR,
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
        "Called by Appointment Service only at booking time. "
        "Requires X-Internal-Key header. "
        "Returns patient details for internal service calls."
    ),
)
async def get_patient_for_service(
    user_id: str,
    x_internal_key: str = Header(...),
) -> InternalPatientResponse:
    """
    Returns patient details for internal service calls.
    """
    _verify_internal_key(x_internal_key)

    object_id = _parse_object_id(user_id)
    user = await get_user_by_id(object_id)

    if user is None or user.role != UserRole.PATIENT:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=PATIENT_NOT_FOUND_ERROR,
        )

    logger.info(f"Internal: patient snapshot fetched for user_id={user_id}")

    return InternalPatientResponse(
        user_id=str(user.id),
        full_name=user.full_name,
        phone_number=user.phone_number,
    )