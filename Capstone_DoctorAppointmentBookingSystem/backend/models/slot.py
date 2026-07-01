from datetime import date, datetime, timezone

from beanie import Document, Indexed, PydanticObjectId
from pydantic import Field
from pymongo import ASCENDING, IndexModel
from enums.slot_status import SlotStatus


class Slot(Document):
    """Represents a doctor's availability slot in the database."""

    doctor_id: PydanticObjectId

    date: date

    start_time: str 
    end_time: str
    
    status: SlotStatus = Field(default=SlotStatus.AVAILABLE)

    created_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )
    
    updated_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )

    class Settings:
        """Beanie configuration for the slots collection."""

        name = "slots"
       
        indexes = [
            # Optimizes doctor slot queries.
            IndexModel(
                [
                    ("doctor_id", ASCENDING),
                    ("date", ASCENDING),
                    ("status", ASCENDING),
                ],
                name="doctor_date_status_index",
            ),
            # Prevents duplicate slots for the same doctor.
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