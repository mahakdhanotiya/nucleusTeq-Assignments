from pydantic import BaseModel


class AppointmentStatsResponse(BaseModel):
    """Appointment-side statistics for the Admin Dashboard."""

    total_appointments: int
    confirmed_appointments: int
    completed_appointments: int
    cancelled_appointments: int
    no_show_appointments: int