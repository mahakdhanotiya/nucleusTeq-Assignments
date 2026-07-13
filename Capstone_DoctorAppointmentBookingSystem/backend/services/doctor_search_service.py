import logging
from datetime import datetime, date
from typing import Optional

from beanie import PydanticObjectId

from services.user_service import internal_fetch_doctor, internal_search_doctors
from repositories.slot_repository import (
    get_available_slots_for_doctor,
    get_slots_by_doctor,
)
from schemas.response.doctor_response import (
    DoctorDetailResponse,
    DoctorSearchResult,
    SlotSummary,
)
from exceptions.custom_exceptions import DoctorNotFoundError

logger = logging.getLogger(__name__)


def filter_future_slots(slots: list) -> list:
    """Helper to filter out past slots based on current local system time."""
    now = datetime.now()
    future_slots = []
    for s in slots:
        try:
            slot_time = datetime.strptime(s.start_time, "%H:%M").time()
            slot_datetime = datetime.combine(s.date, slot_time)
            if slot_datetime > now:
                future_slots.append(s)
        except Exception:
            if s.date >= now.date():
                future_slots.append(s)
    return future_slots


async def search_doctors_service(
    name: Optional[str] = None,
    specialization: Optional[str] = None,
) -> list[DoctorSearchResult]:
    """Returns doctors matching the search criteria."""
    profiles = await internal_search_doctors(name=name, specialization=specialization)

    results: list[DoctorSearchResult] = []
    for profile in profiles:
        doctor_id = PydanticObjectId(profile["user_id"])
        available_slots = await get_available_slots_for_doctor(doctor_id)
        active_slots = filter_future_slots(available_slots)

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
                available_slot_count=len(active_slots),
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
    """Returns doctor details with available slots."""

    try:
        profile = await internal_fetch_doctor(user_id)
    except DoctorNotFoundError:
        raise

    doctor_id = PydanticObjectId(user_id)
    available_slots = await get_available_slots_for_doctor(
        doctor_id=doctor_id,
        slot_date=slot_date,
    )
    active_slots = filter_future_slots(available_slots)

    slot_summaries = [
        SlotSummary(
            id=str(s.id),
            date=s.date,
            start_time=s.start_time,
            end_time=s.end_time,
            status=s.status,
        )
        for s in active_slots
    ]

    logger.info(f"Doctor detail fetched: user_id={user_id}, slots={len(slot_summaries)}")

    doctor_detail_response = DoctorDetailResponse(
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
    return doctor_detail_response