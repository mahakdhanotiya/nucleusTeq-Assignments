from datetime import date, datetime, timezone
from typing import Optional

from beanie import Document, PydanticObjectId
from pydantic import BaseModel, Field
from pymongo import ASCENDING, IndexModel

from enums.appointment_status import AppointmentStatus


class DoctorDetails(BaseModel):
    """Embedded doctor information for an appointment."""
    
    user_id: str
    full_name: str
    specialization: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None


class PatientDetails(BaseModel):
    """Embedded patient information for an appointment."""

    user_id: str
    full_name: str
    phone_number: str


class Appointment(Document):
    """Represents an appointment record with doctor and patient details."""

    patient_id: PydanticObjectId
    doctor_id: PydanticObjectId
    slot_id: PydanticObjectId

    appointment_date: date
    start_time: str
    end_time: str

    status: AppointmentStatus = Field(default=AppointmentStatus.CONFIRMED)

    doctor_details: DoctorDetails
    patient_details: PatientDetails

    payment_id: Optional[PydanticObjectId] = None
    cancelled_at: Optional[datetime] = None
    cancellation_reason: Optional[str] = None

    active: Optional[bool] = Field(default=True)

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    class Settings:
        name = "appointments"
        indexes = [
            IndexModel(
                [("doctor_id", ASCENDING), ("slot_id", ASCENDING)],
                name="doctor_slot_unique_v3",
                unique=True,
                partialFilterExpression={"active": True},
            ),
            IndexModel(
                [("patient_id", ASCENDING), ("status", ASCENDING), ("appointment_date", ASCENDING)],
                name="patient_status_date_index",
            ),
            IndexModel(
                [("doctor_id", ASCENDING), ("appointment_date", ASCENDING), ("status", ASCENDING)],
                name="doctor_date_status_index",
            ),
        ]