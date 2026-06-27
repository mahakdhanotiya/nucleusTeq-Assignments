from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from enums.appointment_status import AppointmentStatus
from enums.payment_status import PaymentStatus


class DoctorSnapshotResponse(BaseModel):
    user_id: str
    full_name: str
    specialization: Optional[str] = None
    consultation_fee: Optional[float] = None
    clinic_address: Optional[str] = None


class PatientSnapshotResponse(BaseModel):
    user_id: str
    full_name: str
    phone_number: str


class PaymentSummary(BaseModel):
    payment_id: str
    status: PaymentStatus
    amount: float
    transaction_ref: str


class AppointmentResponse(BaseModel):
    """Full appointment detail returned after booking or on detail view."""

    id: str
    patient_id: str
    doctor_id: str
    slot_id: str
    appointment_date: date
    start_time: str
    end_time: str
    status: AppointmentStatus
    doctor_snapshot: DoctorSnapshotResponse
    patient_snapshot: PatientSnapshotResponse
    payment: Optional[PaymentSummary] = None
    cancelled_at: Optional[datetime] = None
    cancellation_reason: Optional[str] = None
    created_at: datetime
    updated_at: datetime


class AppointmentCardResponse(BaseModel):
    """
    Lightweight appointment card for list views.
    Used in patient history (FR-10) and doctor dashboard (FR-15).
    """

    id: str
    appointment_date: date
    start_time: str
    end_time: str
    status: AppointmentStatus
    doctor_name: str
    doctor_specialization: Optional[str] = None
    patient_name: str
    patient_phone: str