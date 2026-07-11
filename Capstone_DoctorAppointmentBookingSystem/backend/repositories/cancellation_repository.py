from beanie import PydanticObjectId

from enums.request_status import RequestStatus
from models.cancellation_request import DoctorCancellationRequest


async def create_cancellation_request(
    request: DoctorCancellationRequest,
) -> DoctorCancellationRequest:
    """Inserts a new DoctorCancellationRequest into the database."""
    await request.insert()
    return request


async def get_cancellation_request_by_id(
    request_id: PydanticObjectId,
) -> DoctorCancellationRequest | None:
    """Fetches a DoctorCancellationRequest by its object ID."""
    return await DoctorCancellationRequest.get(request_id)


async def list_cancellation_requests(
    status: RequestStatus | None = None,
) -> list[DoctorCancellationRequest]:
    """Retrieves all cancellation requests, filtered optionally by status."""
    if status is not None:
        return await DoctorCancellationRequest.find(
            DoctorCancellationRequest.status == status
        ).to_list()
    return await DoctorCancellationRequest.find_all().to_list()


async def update_cancellation_request(
    request: DoctorCancellationRequest,
) -> DoctorCancellationRequest:
    """Saves changes made to an existing DoctorCancellationRequest."""
    await request.save()
    return request
