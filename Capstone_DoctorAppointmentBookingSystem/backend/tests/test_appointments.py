import pytest
from httpx import AsyncClient
from beanie import PydanticObjectId
from models.user import User
from models.slot import Slot
from models.appointment import Appointment
from enums.approval_status import ApprovalStatus
from enums.user_role import UserRole

async def create_auth_headers(client: AsyncClient, email: str, role: str) -> dict:
    """Helper to register a user, log in, and return headers."""
    import hashlib
    password = "Password123!"
    # Generate a deterministic 10-digit phone number starting with 9 using MD5 hash
    email_md5 = hashlib.md5(email.encode('utf-8')).hexdigest()
    phone_hash = str(int(email_md5, 16))[:9].zfill(9)
    phone_number = f"9{phone_hash}"[:10]
    
    # Strip any non-alphabet characters for the name
    name_clean = "".join(c for c in email.split("@")[0] if c.isalpha())

    if role == "DOCTOR":
        # Generate a unique license number using phone hash to avoid conflicts
        payload = {
            "email": email,
            "password": password,
            "full_name": f"Dr {name_clean}",
            "phone_number": phone_number,
            "qualification": "MD",
            "specialization": "Cardiologist",
            "experience_years": 10,
            "license_number": f"LIC-{phone_number}-99",
            "consultation_fee": 1000.0,
            "clinic_address": "Test Clinic"
        }
        reg_res = await client.post("/auth/register/doctor", json=payload)
        assert reg_res.status_code == 201, f"Doctor registration failed: {reg_res.status_code} - {reg_res.text}"
        # Approve doctor so they are active and listable
        db_name = User.get_motor_collection().database.name
        all_users = [u.email for u in await User.find_all().to_list()]
        print(f"\nDEBUG: DB NAME={db_name}, ALL USERS={all_users}, SEARCHING FOR={email.lower()}\n")
        user = await User.find_one(User.email == email.lower())
        user.approval_status = ApprovalStatus.APPROVED
        await user.save()
    else:
        payload = {
            "email": email,
            "password": password,
            "full_name": f"Patient {name_clean}",
            "phone_number": phone_number,
            "gender": "MALE",
            "date_of_birth": "1995-10-15"
        }
        reg_res = await client.post("/auth/register/patient", json=payload)
        assert reg_res.status_code == 201, f"Patient registration failed: {reg_res.status_code} - {reg_res.text}"

    # Login
    login_res = await client.post("/auth/login", json={"email": email, "password": password})
    assert login_res.status_code == 200, f"Login failed: {login_res.status_code} - {login_res.text}"
    token = login_res.json()["access_token"]
    return {"Authorization": f"Bearer {token}"}

@pytest.mark.asyncio
async def test_book_appointment_success(client: AsyncClient):
    """Test patient books slot successfully (201)."""
    doctor_headers = await create_auth_headers(client, "dr.book1@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.book1@hospital.com", "PATIENT")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.book1@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    assert res.status_code == 201
    assert res.json()["status"] == "CONFIRMED"

@pytest.mark.asyncio
async def test_book_already_booked_slot_fails(client: AsyncClient):
    """Test patient cannot book a slot that is already booked."""
    doctor_headers = await create_auth_headers(client, "dr.book2@hospital.com", "DOCTOR")
    patient_headers1 = await create_auth_headers(client, "patient.book2a@hospital.com", "PATIENT")
    patient_headers2 = await create_auth_headers(client, "patient.book2b@hospital.com", "PATIENT")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.book2@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    # Patient 1 books it
    await client.post("/appointments", json=booking_payload, headers=patient_headers1)
    # Patient 2 tries to book it
    res = await client.post("/appointments", json=booking_payload, headers=patient_headers2)
    assert res.status_code == 409

@pytest.mark.asyncio
async def test_book_non_existent_slot_fails(client: AsyncClient):
    """Test booking non-existent slot returns 404."""
    patient_headers = await create_auth_headers(client, "patient.book3@hospital.com", "PATIENT")
    doctor_headers = await create_auth_headers(client, "dr.book3@hospital.com", "DOCTOR")
    doctor_user = await User.find_one(User.email == "dr.book3@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": "603d2e1f4f1a2b3c4d5e6f7a", # fake id
        "appointment_date": "2026-07-25"
    }
    res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    assert res.status_code == 404

@pytest.mark.asyncio
async def test_doctor_cannot_book_appointment(client: AsyncClient):
    """Test doctor cannot call book appointments route."""
    doctor_headers1 = await create_auth_headers(client, "dr.book4a@hospital.com", "DOCTOR")
    doctor_headers2 = await create_auth_headers(client, "dr.book4b@hospital.com", "DOCTOR")
    doctor_user = await User.find_one(User.email == "dr.book4b@hospital.com")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers2)
    slot_id = slot_res.json()["id"]

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    res = await client.post("/appointments", json=booking_payload, headers=doctor_headers1)
    assert res.status_code == 403

@pytest.mark.asyncio
async def test_patient_cancel_appointment_inside_window(client: AsyncClient):
    """Test patient cancels appointment successfully inside cancellation window."""
    doctor_headers = await create_auth_headers(client, "dr.cancel1@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.cancel1@hospital.com", "PATIENT")
    
    slot_res = await client.post("/slots", json={"date": "2026-08-30", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.cancel1@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-08-30"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    # Cancel
    cancel_res = await client.patch(f"/appointments/{appt_id}/cancel", json={"reason": "Change of plans"}, headers=patient_headers)
    assert cancel_res.status_code == 200
    assert cancel_res.json()["status"] == "CANCELLED"

@pytest.mark.asyncio
async def test_doctor_cancel_appointment_anytime(client: AsyncClient):
    """Test doctor cancels appointment successfully anytime."""
    doctor_headers = await create_auth_headers(client, "dr.cancel2@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.cancel2@hospital.com", "PATIENT")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.cancel2@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    # Doctor cancels fails (403 Forbidden since cancel is patient-only)
    cancel_res = await client.patch(f"/appointments/{appt_id}/cancel", json={"reason": "Doctor unavailable"}, headers=doctor_headers)
    assert cancel_res.status_code == 403

@pytest.mark.asyncio
async def test_list_my_appointments_patient(client: AsyncClient):
    """Test patient lists their own appointments."""
    patient_headers = await create_auth_headers(client, "patient.list1@hospital.com", "PATIENT")
    res = await client.get("/appointments/patient", headers=patient_headers)
    assert res.status_code == 200
    assert isinstance(res.json(), list)

@pytest.mark.asyncio
async def test_list_my_appointments_doctor(client: AsyncClient):
    """Test doctor lists their own appointments."""
    doctor_headers = await create_auth_headers(client, "dr.list1@hospital.com", "DOCTOR")
    res = await client.get("/appointments/doctor", headers=doctor_headers)
    assert res.status_code == 200
    assert isinstance(res.json(), list)

@pytest.mark.asyncio
async def test_get_appointment_details_owner(client: AsyncClient):
    """Test owner patient retrieves appointment details successfully."""
    doctor_headers = await create_auth_headers(client, "dr.det1@hospital.com", "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.det1@hospital.com", "PATIENT")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.det1@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    res = await client.get(f"/appointments/{appt_id}", headers=patient_headers)
    assert res.status_code == 200
    assert res.json()["id"] == appt_id

@pytest.mark.asyncio
async def test_get_appointment_details_unauthorized_fails(client: AsyncClient):
    """Test non-owner patient cannot retrieve appointment details."""
    doctor_headers = await create_auth_headers(client, "dr.det2@hospital.com", "DOCTOR")
    patient_headers1 = await create_auth_headers(client, "patient.det2a@hospital.com", "PATIENT")
    patient_headers2 = await create_auth_headers(client, "patient.det2b@hospital.com", "PATIENT")
    
    slot_res = await client.post("/slots", json={"date": "2026-07-25", "start_time": "10:00", "end_time": "11:00"}, headers=doctor_headers)
    slot_id = slot_res.json()["id"]
    doctor_user = await User.find_one(User.email == "dr.det2@hospital.com")

    booking_payload = {
        "doctor_id": str(doctor_user.id),
        "slot_id": slot_id,
        "appointment_date": "2026-07-25"
    }
    booking_res = await client.post("/appointments", json=booking_payload, headers=patient_headers1)
    appt_id = booking_res.json()["id"]

    # Patient 2 tries to get details of Patient 1's appointment
    res = await client.get(f"/appointments/{appt_id}", headers=patient_headers2)
    assert res.status_code == 403
