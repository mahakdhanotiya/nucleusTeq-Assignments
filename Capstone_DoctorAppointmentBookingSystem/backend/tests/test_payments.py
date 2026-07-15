import pytest
from httpx import AsyncClient
from beanie import PydanticObjectId
from models.user import User
from models.payment import Payment
from models.appointment import Appointment
from enums.appointment_status import AppointmentStatus
from tests.test_appointments import create_auth_headers

@pytest.mark.asyncio
async def test_process_payment_success(client: AsyncClient):
    """Test processing a payment successfully (200)."""
    doctor_headers = await create_auth_headers(client, "dr.pay1@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.pay1@hospital.com", "PATIENT")

    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.pay1@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    payment_payload = {"payment_method": "MOCK_CARD"}
    res = await client.post(f"/payments/{appt_id}/process", json=payment_payload, headers=patient_headers)
    assert res.status_code == 200
    assert res.json()["status"] == "SUCCESS"

@pytest.mark.asyncio
async def test_process_payment_already_paid_fails(client: AsyncClient):
    """Test paying for an already paid appointment fails."""
    doctor_headers = await create_auth_headers(client, "dr.pay2@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.pay2@hospital.com", "PATIENT")

    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.pay2@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    payment_payload = {"payment_method": "MOCK_CARD"}
    # First payment succeeds
    await client.post(f"/payments/{appt_id}/process", json=payment_payload, headers=patient_headers)
    # Second payment fails
    res = await client.post(f"/payments/{appt_id}/process", json=payment_payload, headers=patient_headers)
    assert res.status_code == 400

@pytest.mark.asyncio
async def test_process_payment_cancelled_appointment_fails(client: AsyncClient):
    """Test processing payment for cancelled appointment fails."""
    doctor_headers = await create_auth_headers(client, "dr.pay3@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.pay3@hospital.com", "PATIENT")

    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.pay3@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    # Cancel appointment
    await client.patch(f"/appointments/{appt_id}/cancel", json={"reason": "Patient cancellling"}, headers=patient_headers)

    payment_payload = {"payment_method": "MOCK_CARD"}
    res = await client.post(f"/payments/{appt_id}/process", json=payment_payload, headers=patient_headers)
    assert res.status_code == 400

@pytest.mark.asyncio
async def test_non_owner_process_payment_unauthorized(client: AsyncClient):
    """Test non-owner patient cannot pay for an appointment."""
    doctor_headers = await create_auth_headers(client, "dr.pay4@hospital.com", "DOCTOR")
    patient_headers1 = await create_auth_headers(client, "patient.pay4a@hospital.com", "PATIENT")
    patient_headers2 = await create_auth_headers(client, "patient.pay4b@hospital.com", "PATIENT")

    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.pay4@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers1)
    appt_id = booking_res.json()["id"]

    payment_payload = {"payment_method": "MOCK_CARD"}
    # Patient 2 tries to pay for Patient 1's appointment
    res = await client.post(f"/payments/{appt_id}/process", json=payment_payload, headers=patient_headers2)
    assert res.status_code == 403

@pytest.mark.asyncio
async def test_get_payment_details(client: AsyncClient):
    """Test getting payment details successfully."""
    doctor_headers = await create_auth_headers(client, "dr.pay5@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.pay5@hospital.com", "PATIENT")

    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.pay5@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    # Process payment
    payment_payload = {"payment_method": "MOCK_CARD"}
    await client.post(f"/payments/{appt_id}/process", json=payment_payload, headers=patient_headers)

    res = await client.get(f"/payments/{appt_id}", headers=patient_headers)
    assert res.status_code == 200
    assert res.json()["status"] == "SUCCESS"
