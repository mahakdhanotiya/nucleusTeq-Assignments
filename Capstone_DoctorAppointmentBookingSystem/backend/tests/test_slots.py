import pytest
from httpx import AsyncClient
from beanie import PydanticObjectId
from models.user import User
from models.slot import Slot
from models.appointment import Appointment
from enums.approval_status import ApprovalStatus
from enums.slot_status import SlotStatus
from tests.test_appointments import create_auth_headers

@pytest.mark.asyncio
async def test_create_slot_success(client: AsyncClient):
    """Test doctor creates availability slot successfully (201)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new1@hospital.com", "DOCTOR")
    slot_payload = {
        "date": "2026-07-25",
        "start_time": "09:00",
        "end_time": "10:00"
    }
    response = await client.post("/slots", json=slot_payload, headers=doctor_headers)
    assert response.status_code == 201
    data = response.json()
    assert data["date"] == "2026-07-25"
    assert data["status"] == "AVAILABLE"
    assert data["start_time"] == "09:00"

@pytest.mark.asyncio
async def test_create_slot_past_date_fails(client: AsyncClient):
    """Test doctor cannot create slot with past date (400)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new2@hospital.com", "DOCTOR")
    slot_payload = {
        "date": "2020-01-01",
        "start_time": "09:00",
        "end_time": "10:00"
    }
    response = await client.post("/slots", json=slot_payload, headers=doctor_headers)
    assert response.status_code == 400
    assert "future" in response.json()["message"].lower()

@pytest.mark.asyncio
async def test_create_slot_invalid_times_fails(client: AsyncClient):
    """Test doctor cannot create slot with end time before start time (422)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new3@hospital.com", "DOCTOR")
    slot_payload = {
        "date": "2026-07-25",
        "start_time": "11:00",
        "end_time": "10:00"
    }
    response = await client.post("/slots", json=slot_payload, headers=doctor_headers)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_create_slot_overlap_fails(client: AsyncClient):
    """Test doctor cannot create overlapping slots (409)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new4@hospital.com", "DOCTOR")
    slot_payload = {
        "date": "2026-07-25",
        "start_time": "14:00",
        "end_time": "15:00"
    }
    await client.post("/slots", json=slot_payload, headers=doctor_headers)
    response = await client.post("/slots", json=slot_payload, headers=doctor_headers)
    assert response.status_code == 409
    assert "overlap" in response.json()["message"].lower()

@pytest.mark.asyncio
async def test_get_my_slots(client: AsyncClient):
    """Test doctor can view their own slots list (200)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new5@hospital.com", "DOCTOR")
    await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    
    response = await client.get("/slots/my", headers=doctor_headers)
    assert response.status_code == 200
    assert len(response.json()) >= 1

@pytest.mark.asyncio
async def test_get_doctor_available_slots_public(client: AsyncClient):
    """Test patient/public can retrieve available slots for a doctor."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new6@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.slot.new6@hospital.com", "PATIENT")
    
    await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    doctor_user = await User.find_one(User.email == "dr.slot.new6@hospital.com")

    response = await client.get(f"/slots/doctor/{doctor_user.id}", headers=patient_headers)
    assert response.status_code == 200
    assert len(response.json()) >= 1

@pytest.mark.asyncio
async def test_update_slot_success(client: AsyncClient):
    """Test doctor updates slot times successfully."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new7@hospital.com", "DOCTOR")
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]

    update_payload = {"start_time": "11:00", "end_time": "12:00"}
    response = await client.put(f"/slots/{slot_id}", json=update_payload, headers=doctor_headers)
    assert response.status_code == 200
    assert response.json()["start_time"] == "11:00"

@pytest.mark.asyncio
async def test_update_slot_past_date_fails(client: AsyncClient):
    """Test doctor cannot update slot to a past date (400)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new8@hospital.com", "DOCTOR")
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]

    update_payload = {"date": "2020-01-01"}
    response = await client.put(f"/slots/{slot_id}", json=update_payload, headers=doctor_headers)
    assert response.status_code == 400

@pytest.mark.asyncio
async def test_update_slot_invalid_times_fails(client: AsyncClient):
    """Test doctor cannot update slot to start time after end time (422)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new9@hospital.com", "DOCTOR")
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]

    update_payload = {"start_time": "15:00", "end_time": "14:00"}
    response = await client.put(f"/slots/{slot_id}", json=update_payload, headers=doctor_headers)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_update_slot_overlap_fails(client: AsyncClient):
    """Test doctor cannot update slot to overlap with another slot (409)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new10@hospital.com", "DOCTOR")
    await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "11:00", "end_time": "12:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]

    update_payload = {"start_time": "09:30", "end_time": "10:30"}
    response = await client.put(f"/slots/{slot_id}", json=update_payload, headers=doctor_headers)
    assert response.status_code == 409

@pytest.mark.asyncio
async def test_delete_slot_success(client: AsyncClient):
    """Test deleting availability slot successfully."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new11@hospital.com", "DOCTOR")
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]

    response = await client.delete(f"/slots/{slot_id}", headers=doctor_headers)
    assert response.status_code == 200
    assert response.json()["message"] == "Slot deleted successfully."

@pytest.mark.asyncio
async def test_delete_slot_unauthorized(client: AsyncClient):
    """Test another doctor cannot delete a doctor's slot."""
    doctor_headers1 = await create_auth_headers(client, "dr.slot.new12a@hospital.com", "DOCTOR")
    doctor_headers2 = await create_auth_headers(client, "dr.slot.new12b@hospital.com", "DOCTOR")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers1)
    slot_id = slot_res.json()["id"]

    response = await client.delete(f"/slots/{slot_id}", headers=doctor_headers2)
    assert response.status_code == 403

@pytest.mark.asyncio
async def test_delete_booked_slot_fails(client: AsyncClient):
    """Test doctor cannot delete a booked slot."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new13@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.slot.new13@hospital.com", "PATIENT")

    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.slot.new13@hospital.com")

    # Book the slot
    await client.post("/appointments", json={
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }, headers=patient_headers)

    response = await client.delete(f"/slots/{slot_id}", headers=doctor_headers)
    assert response.status_code == 409

@pytest.mark.asyncio
async def test_patient_create_slot_forbidden(client: AsyncClient):
    """Test patient is forbidden from creating slots (403)."""
    patient_headers = await create_auth_headers(client, "patient.slot.new14@hospital.com", "PATIENT")
    response = await client.post("/slots", json={"date": "2026-07-25", "start_time": "09:00", "end_time": "10:00"}, headers=patient_headers)
    assert response.status_code == 403

@pytest.mark.asyncio
async def test_create_slot_missing_fields_validation(client: AsyncClient):
    """Test creating a slot with missing required fields fails validation (422)."""
    doctor_headers = await create_auth_headers(client, "dr.slot.new15@hospital.com", "DOCTOR")
    response = await client.post("/slots", json={"date": "2026-07-25"}, headers=doctor_headers)
    assert response.status_code == 422
