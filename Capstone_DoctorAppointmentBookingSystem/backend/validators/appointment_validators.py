import re
from datetime import date

def validate_cancellation_date(value: date) -> date:
    """Ensures cancellation date is today or in the future."""
    if value < date.today():
        raise ValueError("Date must be today or a future date.")
    return value

def validate_cancellation_time(value: str) -> str:
    """Ensures cancellation time string is in valid HH:MM format."""
    if not re.match(r"^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", value):
        raise ValueError("Time must be in HH:MM format.")
    return value
