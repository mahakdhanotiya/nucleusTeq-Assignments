from constants.slot_constants import (
    SLOT_NOT_FOUND_TEMPLATE,
    SLOT_CONFLICT_ERROR,
    SLOT_NOT_AVAILABLE_ERROR,
    SLOT_NOT_OWNED_BY_DOCTOR_ERROR,
    END_TIME_MUST_BE_AFTER_START,
    SLOT_DATE_MUST_BE_TODAY_OR_FUTURE,
)


class SlotNotFoundException(Exception):
    """Raised when a slot is not found."""

    def __init__(self, slot_id: str = ""):
        self.slot_id = slot_id
        super().__init__(SLOT_NOT_FOUND_TEMPLATE.format(slot_id))


class SlotConflictError(Exception):
    """Raised when a slot overlaps with an existing slot."""

    def __init__(self):
        super().__init__(SLOT_CONFLICT_ERROR)


class SlotNotAvailableError(Exception):
    """Raised when a slot is not available."""

    def __init__(self):
        super().__init__(SLOT_NOT_AVAILABLE_ERROR)


class SlotNotOwnedByDoctorError(Exception):
    """Raised when a doctor accesses another doctor's slot."""

    def __init__(self):
        super().__init__(SLOT_NOT_OWNED_BY_DOCTOR_ERROR)


class InvalidSlotTimeError(Exception):
    """Raised when the slot time is invalid."""

    def __init__(self):
        super().__init__(END_TIME_MUST_BE_AFTER_START)


class PastSlotDateError(Exception):
    """Raised when the slot date is in the past."""

    def __init__(self):
        super().__init__(SLOT_DATE_MUST_BE_TODAY_OR_FUTURE)

