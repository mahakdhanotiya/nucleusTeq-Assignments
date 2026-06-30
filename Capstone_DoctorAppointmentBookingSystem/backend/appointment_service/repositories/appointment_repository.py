from datetime import date, datetime, timezone

from beanie import PydanticObjectId

from enums.appointment_status import AppointmentStatus
from models.appointment import Appointment


async def create_appointment(appointment: Appointment) -> Appointment:
    """Persists a new Appointment document."""
    await appointment.insert()
    return appointment


async def get_appointment_by_id(appointment_id: PydanticObjectId) -> Appointment | None:
    """Fetches a single appointment by its _id."""
    return await Appointment.get(appointment_id)


async def get_appointments_by_patient(
    patient_id: PydanticObjectId,
    status: AppointmentStatus | None = None,
) -> list[Appointment]:
    """Returns appointments for a patient."""
    
    filters = [Appointment.patient_id == patient_id]
    if status is not None:
        filters.append(Appointment.status == status)
    return await Appointment.find(*filters).sort(
        [("appointment_date", 1), ("start_time", 1)]
    ).to_list()


async def get_appointments_by_doctor(
    doctor_id: PydanticObjectId,
    appointment_date: date | None = None,
    status: AppointmentStatus | None = None,
) -> list[Appointment]:
    """Returns appointments for a patient."""
    
    filters = [Appointment.doctor_id == doctor_id]
    if appointment_date is not None:
        filters.append(Appointment.appointment_date == appointment_date)
    if status is not None:
        filters.append(Appointment.status == status)
    return await Appointment.find(*filters).sort(
        [("appointment_date", 1), ("start_time", 1)]
    ).to_list()


async def get_upcoming_appointments_by_doctor(
    doctor_id: PydanticObjectId,
) -> list[Appointment]:
    """Returns all CONFIRMED future appointments for a doctor."""
    today = date.today()
    return await Appointment.find(
        Appointment.doctor_id == doctor_id,
        Appointment.status == AppointmentStatus.CONFIRMED,
        {"appointment_date": {"$gte": today}},
    ).sort([("appointment_date", 1), ("start_time", 1)]).to_list()


async def get_todays_appointments_by_doctor(
    doctor_id: PydanticObjectId,
) -> list[Appointment]:
    """Returns all of today's appointments for a doctor regardless of status."""
    today = date.today()
    return await Appointment.find(
        Appointment.doctor_id == doctor_id,
        Appointment.appointment_date == today,
    ).sort([("start_time", 1)]).to_list()


async def update_appointment(appointment: Appointment) -> Appointment:
    """Persists changes to an existing Appointment document."""
    appointment.updated_at = datetime.now(timezone.utc)
    await appointment.save()
    return appointment