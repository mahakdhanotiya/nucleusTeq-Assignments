# Key design: doctor_snapshot and patient_snapshot are embedded at booking
# time so appointment history reads never require cross-service calls.
# The unique compound index on (doctor_id, slot_id) is the database-level
# guarantee that prevents double booking under concurrent requests.


from datetime import date, datetime, timezone
from typing import Optional

from beanie import Document, PydanticObjectId
from pydantic import BaseModel, Field
from pymongo import ASCENDING, IndexModel

from enums.appointment_status import AppointmentStatus


class DoctorSnapshot(BaseModel):
    """Immutable copy of doctor profile captured at booking time (FR-7)."""

    user_id: str
    full_name: str
    specialization: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None


class PatientSnapshot(BaseModel):
    """Immutable copy of patient info captured at booking time (FR-7, FR-18)."""

    user_id: str
    full_name: str
    phone_number: str


class Appointment(Document):
    """
    MongoDB document representing a confirmed appointment booking.

    Embeds doctor and patient snapshots so history reads are self-contained.
    Status transitions are enforced in appointment_service.py:
        CONFIRMED → COMPLETED  (doctor marks after appointment time)
        CONFIRMED → CANCELLED  (patient cancels ≥2 hours before)
        CONFIRMED → NO_SHOW    (doctor marks after appointment time)
    """

    patient_id: PydanticObjectId
    doctor_id: PydanticObjectId
    slot_id: PydanticObjectId

    appointment_date: date
    start_time: str
    end_time: str

    status: AppointmentStatus = Field(default=AppointmentStatus.CONFIRMED)

    doctor_snapshot: DoctorSnapshot
    patient_snapshot: PatientSnapshot

    payment_id: Optional[PydanticObjectId] = None
    cancelled_at: Optional[datetime] = None
    cancellation_reason: Optional[str] = None

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "appointments"
        indexes = [
            # Prevents double booking at the database level (SRS NFR).
            # Two concurrent requests cannot both insert for the same slot.
            IndexModel(
                [("doctor_id", ASCENDING), ("slot_id", ASCENDING)],
                name="doctor_slot_unique",
                unique=True,
            ),
            # Serves patient appointment history filtered by status (FR-10).
            IndexModel(
                [("patient_id", ASCENDING), ("status", ASCENDING), ("appointment_date", ASCENDING)],
                name="patient_status_date_index",
            ),
            # Serves doctor appointment dashboard views (FR-15).
            IndexModel(
                [("doctor_id", ASCENDING), ("appointment_date", ASCENDING), ("status", ASCENDING)],
                name="doctor_date_status_index",
            ),
        ]