from constants.doctor_constants import (
    DOCTOR_NOT_FOUND_TEMPLATE,
)


class DoctorNotFoundError(Exception):
    """Raised when a doctor is not found."""

    def __init__(self, user_id: str = ""):
        self.user_id = user_id
        super().__init__(DOCTOR_NOT_FOUND_TEMPLATE.format(user_id))