import re
from datetime import date
from constants.slot_constants import INVALID_TIME_FORMAT, END_TIME_MUST_BE_AFTER_START
from exceptions.custom_exceptions import PastSlotDateError, InvalidSlotTimeError

def is_valid_time(value: str) -> bool:
    """Returns True if the value matches HH:MM 24-hour format."""
    return bool(re.fullmatch(r"^([01]\d|2[0-3]):[0-5]\d$", value))

def validate_time_format(value: str) -> str:
    """Ensures a time string is in valid HH:MM 24-hour format."""
    if not is_valid_time(value):
        raise ValueError(INVALID_TIME_FORMAT)
    return value

def validate_end_after_start(start_time: str, end_time: str) -> None:
    """Validates the slot time range."""
    if start_time and end_time:
        if end_time <= start_time:
            raise ValueError(END_TIME_MUST_BE_AFTER_START)

def validate_slot_date(slot_date: date) -> None:
    """Validates that slot date is not in the past."""
    if slot_date < date.today():
        raise PastSlotDateError()
