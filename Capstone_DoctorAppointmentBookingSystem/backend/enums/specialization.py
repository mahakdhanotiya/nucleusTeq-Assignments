from enum import Enum


class Specialization(str, Enum):
    """Specialization options for doctors."""

    CARDIOLOGIST = "Cardiologist"
    DERMATOLOGIST = "Dermatologist"
    DENTIST = "Dentist"
    NEUROLOGIST = "Neurologist"
    ORTHOPEDIC = "Orthopedic"
    PEDIATRICIAN = "Pediatrician"
    GENERAL_PHYSICIAN = "General Physician"
    GYNECOLOGIST = "Gynecologist"
