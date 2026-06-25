# Defines the approval lifecycle for doctor accounts.
# Only DOCTOR role accounts go through this workflow.
# PATIENT and ADMIN accounts are always implicitly APPROVED.

from enum import Enum


class ApprovalStatus(str, Enum):
    """
    Approval states for doctor accounts.

    PENDING  — default for all new doctor registrations.
               Doctor cannot log in until an Admin approves them.
    APPROVED — Admin has approved this doctor.
               Doctor can log in, manage slots, and appear in search.
    REJECTED — Admin has rejected this doctor's registration.
               Doctor cannot log in.
    """

    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"