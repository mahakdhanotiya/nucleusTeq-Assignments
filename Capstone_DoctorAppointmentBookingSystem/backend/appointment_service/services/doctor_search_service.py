# Business logic for FR-5 (doctor search) and FR-6 (doctor detail view).
#
# This service composes responses from two data sources:
#   - Doctor profile data  → User Service (via user_service_client)
#   - Available slot data  → local `slots` collection (via slot_repository)

import httpx
import logging
from datetime import date
from typing import Optional

from beanie import PydanticObjectId

from integrations.user_service_client import fetch_doctor, search_doctors
from repositories.slot_repository import (
    get_available_slots_for_doctor,
    get_slots_by_doctor,
)
from schemas.response.doctor_response import (
    DoctorDetailResponse,
    DoctorSearchResult,
    SlotSummary,
)
from exceptions.appointment_exceptions import DoctorNotFoundError

logger = logging.getLogger(__name__)


async def search_doctors_service(
    name: Optional[str] = None,
    specialization: Optional[str] = None,
) -> list[DoctorSearchResult]:
    """
    Returns a list of doctors matching the search criteria (FR-5).

    Fetches matching doctor profiles from User Service (APPROVED + active only),
    then enriches each result with the count of available slots from local DB.
    """
    profiles = await search_doctors(name=name, specialization=specialization)

    results: list[DoctorSearchResult] = []
    for profile in profiles:
        doctor_id = PydanticObjectId(profile["user_id"])
        available_slots = await get_available_slots_for_doctor(doctor_id)

        results.append(
            DoctorSearchResult(
                user_id=profile["user_id"],
                full_name=profile["full_name"],
                specialization=profile.get("specialization"),
                qualification=profile.get("qualification"),
                experience_years=profile.get("experience_years"),
                consultation_fee=profile.get("consultation_fee"),
                clinic_address=profile.get("clinic_address"),
                profile_photo_url=profile.get("profile_photo_url"),
                available_slot_count=len(available_slots),
            )
        )

    logger.info(
        f"Doctor search: name={name!r}, specialization={specialization!r}, "
        f"results={len(results)}"
    )
    return results


async def get_doctor_detail(
    user_id: str,
    slot_date: Optional[date] = None,
) -> DoctorDetailResponse:
    """
    Returns the full doctor profile with available slots (FR-6).

    Fetches the doctor profile from User Service and combines it with
    available slots from the local slots collection.
    An optional `slot_date` filter narrows the slot list to a specific day.

    Raises DoctorNotFoundError if User Service returns 404.
    """

    try:
        profile = await fetch_doctor(user_id)
    except httpx.HTTPStatusError as exc:
        if exc.response.status_code == 404:
            raise DoctorNotFoundError(user_id)
        raise

    doctor_id = PydanticObjectId(user_id)
    available_slots = await get_available_slots_for_doctor(
        doctor_id=doctor_id,
        slot_date=slot_date,
    )

    slot_summaries = [
        SlotSummary(
            id=str(s.id),
            date=s.date,
            start_time=s.start_time,
            end_time=s.end_time,
            status=s.status,
        )
        for s in available_slots
    ]

    logger.info(f"Doctor detail fetched: user_id={user_id}, slots={len(slot_summaries)}")

    return DoctorDetailResponse(
        user_id=profile["user_id"],
        full_name=profile["full_name"],
        specialization=profile.get("specialization"),
        qualification=profile.get("qualification"),
        experience_years=profile.get("experience_years"),
        consultation_fee=profile.get("consultation_fee"),
        clinic_address=profile.get("clinic_address"),
        profile_photo_url=profile.get("profile_photo_url"),
        available_slots=slot_summaries,
    )