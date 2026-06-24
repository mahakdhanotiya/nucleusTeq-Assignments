from datetime import date, datetime, timezone

from beanie import Document, Indexed, PydanticObjectId
from pydantic import Field
from pymongo import ASCENDING, IndexModel


class Slot(Document):
    """
    MongoDB document representing one doctor availability slot.

    Each slot belongs to exactly one doctor and covers a specific
    date + time window. Its `status` field controls whether a patient
    can book it:
      - AVAILABLE → can be booked
      - BOOKED    → already taken; cannot be modified or deleted (FR-14)
    """

    # The doctor who created this slot.
    # References users._id in User Service (cross-service reference).
    # Stored as PydanticObjectId so MongoDB saves it as a native ObjectId,
    # not a plain string — consistent with how User Service stores user IDs.
    doctor_id: Indexed(PydanticObjectId)

    # Calendar date of the slot (e.g. 2025-09-15).
    # Stored as Python `date`, serialised as YYYY-MM-DD in API responses.
    date: date

    # Start and end times in 24-hour "HH:MM" format (e.g. "09:00", "17:30").
    # Validated in slot_service.py: end_time must always be after start_time.
    start_time: str = Field(..., pattern=r"^\d{2}:\d{2}$")
    end_time: str = Field(..., pattern=r"^\d{2}:\d{2}$")

    # Current lifecycle state of this slot.
    # Imported from enums/slot_status.py.
    # Default is AVAILABLE when a doctor first creates a slot.
    status: str = Field(default="AVAILABLE")

    # Audit timestamps — set automatically, never sent by the client.
    created_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )
    updated_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )

    class Settings:
        """Beanie configuration for the slots collection."""

        # The MongoDB collection name.
        name = "slots"

        indexes = [
            # --- Primary query index ---
            # Covers the most common read: "give me all AVAILABLE slots
            # for doctor X on date Y". All three fields are used together
            # in every slot availability query, so a compound index on all
            # three is much more efficient than three separate single-field
            # indexes.
            IndexModel(
                [
                    ("doctor_id", ASCENDING),
                    ("date", ASCENDING),
                    ("status", ASCENDING),
                ],
                name="doctor_date_status_index",
            ),
            # --- Duplicate prevention index ---
            # Prevents a doctor from creating two slots that start at the
            # same time on the same day. This is enforced at the database
            # level, not just in application code, so it holds even under
            # concurrent requests.
            IndexModel(
                [
                    ("doctor_id", ASCENDING),
                    ("date", ASCENDING),
                    ("start_time", ASCENDING),
                ],
                name="doctor_date_starttime_unique",
                unique=True,
            ),
        ]