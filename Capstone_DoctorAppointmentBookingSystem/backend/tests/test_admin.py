import pytest
from httpx import AsyncClient
from beanie import PydanticObjectId
from models.user import User
from models.slot import Slot
from models.appointment import Appointment
from models.cancellation_request import DoctorCancellationRequest
from enums.approval_status import ApprovalStatus
from enums.appointment_status import AppointmentStatus
from tests.test_appointments import create_auth_headers
import pytest_asyncio

@pytest_asyncio.fixture
async def admin_headers(client: AsyncClient, clean_collections) -> dict:
    """Fixture to create and return admin authorization headers."""
    from utils.password import hash_password
    admin_user = User(
        full_name="Admin User",
        email="system.admin@hospital.com",
        password_hash=hash_password("Hash123!"),
        phone_number="9999999999",
        role="ADMIN",
        is_active=True
    )
    await admin_user.insert()
    login_res = await client.post("/auth/login", json={"email": "system.admin@hospital.com", "password": "Hash123!"})
    token = login_res.json()["access_token"]
    return {"Authorization": f"Bearer {token}"}

@pytest.mark.asyncio
async def test_admin_doctor_approval_and_rejection_flow(client: AsyncClient, admin_headers: dict):
    """Test admin listing all doctors and approving/rejecting accounts."""
    # 1. Register a doctor (starts as PENDING)
    payload = {
        "email": "dr.pending@hospital.com",
        "password": "Password123!",
        "full_name": "Dr Pending",
        "phone_number": "7766554433",
        "qualification": "MBBS",
        "specialization": "Dermatologist",
        "experience_years": 4,
        "license_number": "LIC-PENDING-123",
        "consultation_fee": 800.0,
        "clinic_address": "Derma Care"
    }
    await client.post("/auth/register/doctor", json=payload)
    
    # Verify doctor is in list
    list_res = await client.get("/admin/doctors", headers=admin_headers)
    assert list_res.status_code == 200
    doctors_list = list_res.json()
    doctor_entry = next(d for d in doctors_list if d["email"] == payload["email"])
    assert doctor_entry["approval_status"] == "PENDING"
    doctor_user_id = doctor_entry["user_id"]

    # 2. Admin approves doctor
    approve_res = await client.patch(f"/admin/doctors/{doctor_user_id}/approve", headers=admin_headers)
    assert approve_res.status_code == 200
    assert approve_res.json()["message"] == "Doctor account successfully approved."

    # Verify updated in DB
    user = await User.find_one(User.id == PydanticObjectId(doctor_user_id))
    assert user.approval_status == ApprovalStatus.APPROVED

@pytest.mark.asyncio
async def test_admin_doctor_rejection(client: AsyncClient, admin_headers: dict):
    """Test admin rejecting a pending doctor account."""
    payload = {
        "email": "dr.rejected@hospital.com",
        "password": "Password123!",
        "full_name": "Dr Rejected",
        "phone_number": "7766554434",
        "qualification": "MBBS",
        "specialization": "Dermatologist",
        "experience_years": 3,
        "license_number": "LIC-REJECT-123",
        "consultation_fee": 800.0,
        "clinic_address": "Derma Care"
    }
    await client.post("/auth/register/doctor", json=payload)
    
    # Get doctor ID
    list_res = await client.get("/admin/doctors", headers=admin_headers)
    doctors_list = list_res.json()
    doctor_entry = next(d for d in doctors_list if d["email"] == payload["email"])
    doctor_user_id = doctor_entry["user_id"]

    # Admin rejects
    reject_res = await client.patch(f"/admin/doctors/{doctor_user_id}/reject", headers=admin_headers)
    assert reject_res.status_code == 200
    assert reject_res.json()["message"] == "Doctor account successfully rejected."

    user = await User.find_one(User.id == PydanticObjectId(doctor_user_id))
    assert user.approval_status == ApprovalStatus.REJECTED

@pytest.mark.asyncio
async def test_doctor_leave_request_flow(client: AsyncClient, admin_headers: dict):
    """Test doctor submitting leave request, admin approving it, and automatic slot/appointment cancellation."""
    dr_email = "dr.leave@hospital.com"
    doctor_headers = await create_auth_headers(client, dr_email, "DOCTOR")
    patient_headers = await create_auth_headers(client, "patient.leave@hospital.com", "PATIENT")

    doctor_user = await User.find_one(User.email == dr_email)
    doctor_id = str(doctor_user.id)

    # 1. Create a slot
    slot_res = await client.post("/slots", json={
        "date": "2026-08-15",
        "start_time": "10:00",
        "end_time": "11:00"
    }, headers=doctor_headers)
    slot_id = slot_res.json()["id"]

    # 2. Book appointment on that slot
    booking_res = await client.post("/appointments", json={
        "doctor_id": doctor_id,
        "slot_id": slot_id,
        "appointment_date": "2026-08-15"
    }, headers=patient_headers)
    appt_id = booking_res.json()["id"]

    # 3. Doctor submits leave request covering that duration
    leave_payload = {
        "date": "2026-08-15",
        "start_time": "09:00",
        "end_time": "12:00",
        "reason": "Family Emergency"
    }
    leave_res = await client.post("/appointments/doctor/cancel-request", json=leave_payload, headers=doctor_headers)
    assert leave_res.status_code == 201
    leave_data = leave_res.json()
    assert leave_data["reason"] == "Family Emergency"
    leave_req_id = leave_data["id"]

    # 4. Admin views leave requests
    reqs_res = await client.get("/appointments/admin/cancel-requests", headers=admin_headers)
    assert reqs_res.status_code == 200
    assert len(reqs_res.json()) >= 1

    # 5. Admin approves leave request (PUT request)
    approve_res = await client.put(f"/appointments/admin/cancel-requests/{leave_req_id}/approve", headers=admin_headers)
    assert approve_res.status_code == 200
    assert approve_res.json()["status"] == "APPROVED"

    # Verify slot is DELETED
    slot = await Slot.find_one(Slot.id == PydanticObjectId(slot_id))
    assert slot is None

    # Verify appointment is CANCELLED and has cancellation reason
    appt = await Appointment.find_one(Appointment.id == PydanticObjectId(appt_id))
    assert appt.status == AppointmentStatus.CANCELLED
    assert appt.cancellation_reason == "Cancelled due to doctor leave request: Family Emergency"

@pytest.mark.asyncio
async def test_doctor_leave_request_rejection(client: AsyncClient, admin_headers: dict):
    """Test admin rejecting a doctor leave request."""
    dr_email = "dr.leave.reject@hospital.com"
    doctor_headers = await create_auth_headers(client, dr_email, "DOCTOR")
    
    leave_payload = {
        "date": "2026-08-16",
        "start_time": "09:00",
        "end_time": "12:00",
        "reason": "Routine Checkup"
    }
    leave_res = await client.post("/appointments/doctor/cancel-request", json=leave_payload, headers=doctor_headers)
    leave_req_id = leave_res.json()["id"]

    # Admin rejects
    reject_res = await client.put(f"/appointments/admin/cancel-requests/{leave_req_id}/reject", headers=admin_headers)
    assert reject_res.status_code == 200
    assert reject_res.json()["status"] == "REJECTED"

@pytest.mark.asyncio
async def test_unauthorized_admin_routes_fails(client: AsyncClient):
    """Test non-admin user cannot access admin endpoints."""
    patient_headers = await create_auth_headers(client, "patient.nonadmin@hospital.com", "PATIENT")
    
    res1 = await client.get("/admin/doctors", headers=patient_headers)
    assert res1.status_code == 403

    res2 = await client.get("/appointments/admin/cancel-requests", headers=patient_headers)
    assert res2.status_code == 403

@pytest.mark.asyncio
async def test_admin_dashboard_stats(client: AsyncClient, admin_headers: dict):
    """Test admin can retrieve user and appointment dashboard statistics."""
    res = await client.get("/admin/dashboard/users", headers=admin_headers)
    assert res.status_code == 200
    assert "total_patients" in res.json()
    assert "total_doctors" in res.json()

    res2 = await client.get("/admin/dashboard/appointments", headers=admin_headers)
    assert res2.status_code == 200
    assert "total_appointments" in res2.json()
