# HTTP client for all outbound calls to User Service internal APIs.
#
# This is the ONLY file in Appointment Service that knows User Service's
# URL or internal key. If User Service moves or the key rotates, only
# this file changes.
#
# All functions raise httpx.HTTPStatusError on non-2xx responses,
# which is caught by callers and translated to domain exceptions.

import logging
from typing import Optional

import httpx

from constants.settings import settings

logger = logging.getLogger(__name__)

# Shared headers sent on every internal request.
_INTERNAL_HEADERS = {"x-internal-key": settings.INTERNAL_API_KEY}


async def fetch_doctor(user_id: str) -> dict:
    """
    Fetches a single doctor's profile from User Service.

    Called at:
      - Doctor detail view (FR-6)
      - Booking time to build doctor_snapshot (FR-7)

    Returns the InternalDoctorResponse payload as a dict.
    Raises httpx.HTTPStatusError on 404 / 403 / 5xx.
    """
    url = f"{settings.USER_SERVICE_BASE_URL}/internal/doctors/{user_id}"
    async with httpx.AsyncClient(timeout=5.0) as client:
        response = await client.get(url, headers=_INTERNAL_HEADERS)
        response.raise_for_status()
        logger.debug(f"fetch_doctor: user_id={user_id}")
        return response.json()


async def search_doctors(
    name: Optional[str] = None,
    specialization: Optional[str] = None,
) -> list[dict]:
    """
    Searches approved, active doctors via User Service.

    Called by doctor_search_service to power FR-5 (search by name/specialization).
    User Service owns the filtering logic and only returns APPROVED + active doctors.

    Returns a list of InternalDoctorResponse payloads as dicts.
    """
    url = f"{settings.USER_SERVICE_BASE_URL}/internal/doctors/search"
    params: dict = {}
    if name:
        params["name"] = name
    if specialization:
        params["specialization"] = specialization

    async with httpx.AsyncClient(timeout=5.0) as client:
        response = await client.get(url, headers=_INTERNAL_HEADERS, params=params)
        response.raise_for_status()
        logger.debug(f"search_doctors: name={name!r}, specialization={specialization!r}")
        return response.json()


async def fetch_patient(user_id: str) -> dict:
    """
    Fetches a patient's snapshot data from User Service.

    Called at booking time (FR-7) to build the patient_snapshot
    stored inside the appointment document.

    Returns the InternalPatientResponse payload as a dict.
    Raises httpx.HTTPStatusError on 404 / 403 / 5xx.
    """
    url = f"{settings.USER_SERVICE_BASE_URL}/internal/patients/{user_id}"
    async with httpx.AsyncClient(timeout=5.0) as client:
        response = await client.get(url, headers=_INTERNAL_HEADERS)
        response.raise_for_status()
        logger.debug(f"fetch_patient: user_id={user_id}")
        return response.json()