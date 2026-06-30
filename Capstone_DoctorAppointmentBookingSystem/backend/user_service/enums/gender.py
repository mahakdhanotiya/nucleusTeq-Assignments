from enum import Enum


class Gender(str, Enum):
    """Gender options for patient registration."""

    MALE = "MALE"
    FEMALE = "FEMALE"
    OTHER = "OTHER"