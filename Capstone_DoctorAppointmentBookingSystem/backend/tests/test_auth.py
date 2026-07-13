import pytest
from httpx import AsyncClient
from models.user import User
from enums.user_role import UserRole
from enums.approval_status import ApprovalStatus

@pytest.mark.asyncio
async def test_patient_registration_success(client: AsyncClient):
    """Test successful patient registration."""
    payload = {
        "email": "testpatient.new@hospital.com",
        "password": "Password123!",
        "full_name": "Test Patient",
        "phone_number": "9876543210",
        "gender": "MALE",
        "date_of_birth": "1995-10-15"
    }
    response = await client.post("/auth/register/patient", json=payload)
    assert response.status_code == 201
    assert response.json()["message"] == "Registration successful. Please log in."

    # Verify in DB
    user = await User.find_one(User.email == payload["email"])
    assert user is not None
    assert user.role == UserRole.PATIENT
    assert user.approval_status == ApprovalStatus.APPROVED

@pytest.mark.asyncio
async def test_patient_registration_missing_fields(client: AsyncClient):
    """Test registration validation fails when required fields are missing."""
    payload = {
        "email": "missing.fields@hospital.com",
        "password": "Password123!"
    }
    response = await client.post("/auth/register/patient", json=payload)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_patient_registration_invalid_phone(client: AsyncClient):
    """Test registration fails with invalid phone number format."""
    payload = {
        "email": "invalid.phone@hospital.com",
        "password": "Password123!",
        "full_name": "Patient Phone",
        "phone_number": "12345", # invalid length
        "gender": "MALE",
        "date_of_birth": "1995-10-15"
    }
    response = await client.post("/auth/register/patient", json=payload)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_patient_registration_invalid_email(client: AsyncClient):
    """Test registration fails with invalid email format."""
    payload = {
        "email": "invalidemail.com", # no @
        "password": "Password123!",
        "full_name": "Patient Email",
        "phone_number": "9876543210",
        "gender": "MALE",
        "date_of_birth": "1995-10-15"
    }
    response = await client.post("/auth/register/patient", json=payload)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_patient_registration_invalid_gender(client: AsyncClient):
    """Test registration fails with invalid gender enum value."""
    payload = {
        "email": "invalid.gender@hospital.com",
        "password": "Password123!",
        "full_name": "Patient Gender",
        "phone_number": "9876543210",
        "gender": "OTHER_INVALID",
        "date_of_birth": "1995-10-15"
    }
    response = await client.post("/auth/register/patient", json=payload)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_doctor_registration_success(client: AsyncClient):
    """Test successful doctor registration with pending approval status."""
    payload = {
        "email": "testdoctor.new@hospital.com",
        "password": "Password123!",
        "full_name": "Test Doctor",
        "phone_number": "8765432109",
        "qualification": "MBBS, MD",
        "specialization": "Cardiologist",
        "experience_years": 8,
        "license_number": "LIC-9988-ABC",
        "consultation_fee": 1200.0,
        "clinic_address": "Heart Clinic, Mumbai"
    }
    response = await client.post("/auth/register/doctor", json=payload)
    assert response.status_code == 201
    assert response.json()["message"] == "Registration successful. Please log in."

    # Verify in DB
    user = await User.find_one(User.email == payload["email"])
    assert user is not None
    assert user.role == UserRole.DOCTOR
    assert user.approval_status == ApprovalStatus.PENDING

@pytest.mark.asyncio
async def test_doctor_registration_missing_fields(client: AsyncClient):
    """Test doctor registration fails when missing specialization details."""
    payload = {
        "email": "missing.dr@hospital.com",
        "password": "Password123!",
        "full_name": "Dr Missing",
        "phone_number": "8765432109",
        "qualification": "MBBS",
        # missing specialization, experience_years, etc.
    }
    response = await client.post("/auth/register/doctor", json=payload)
    assert response.status_code == 422

@pytest.mark.asyncio
async def test_registration_duplicate_email(client: AsyncClient):
    """Test registration fails with duplicate email address."""
    payload = {
        "email": "duplicate@hospital.com",
        "password": "Password123!",
        "full_name": "User One",
        "phone_number": "9876543210",
        "gender": "MALE",
        "date_of_birth": "1995-10-15"
    }
    await client.post("/auth/register/patient", json=payload)

    payload2 = {
        "email": "duplicate@hospital.com",
        "password": "Pass123!",
        "full_name": "User Two",
        "phone_number": "9876543211",
        "gender": "FEMALE",
        "date_of_birth": "1996-10-15"
    }
    response = await client.post("/auth/register/patient", json=payload2)
    assert response.status_code == 409
    assert "already exists" in response.json()["message"]

@pytest.mark.asyncio
async def test_login_success(client: AsyncClient):
    """Test successful login and JWT validation."""
    payload = {
        "email": "loginuser@hospital.com",
        "password": "Password123!",
        "full_name": "Login User",
        "phone_number": "9988776655",
        "gender": "MALE",
        "date_of_birth": "1995-10-15"
    }
    await client.post("/auth/register/patient", json=payload)

    login_payload = {
        "email": payload["email"],
        "password": payload["password"]
    }
    response = await client.post("/auth/login", json=login_payload)
    assert response.status_code == 200
    data = response.json()
    assert "access_token" in data
    assert data["user"]["email"] == payload["email"]

@pytest.mark.asyncio
async def test_login_invalid_password(client: AsyncClient):
    """Test login fails with incorrect password."""
    payload = {
        "email": "loginuser2@hospital.com",
        "password": "Password123!",
        "full_name": "Login User Two",
        "phone_number": "9988776655",
        "gender": "MALE",
        "date_of_birth": "1995-10-15"
    }
    await client.post("/auth/register/patient", json=payload)

    login_payload = {
        "email": payload["email"],
        "password": "WrongPassword!"
    }
    response = await client.post("/auth/login", json=login_payload)
    assert response.status_code == 401
    assert "Invalid email or password" in response.json()["message"]

@pytest.mark.asyncio
async def test_login_non_existent_email(client: AsyncClient):
    """Test login fails with email that does not exist."""
    login_payload = {
        "email": "nonexistent@hospital.com",
        "password": "Password123!"
    }
    response = await client.post("/auth/login", json=login_payload)
    assert response.status_code == 401

@pytest.mark.asyncio
async def test_login_unapproved_doctor(client: AsyncClient):
    """Test doctor login fails when their account is not approved by admin."""
    payload = {
        "email": "unapproved.dr@hospital.com",
        "password": "Password123!",
        "full_name": "Unapproved Doctor",
        "phone_number": "8765432109",
        "qualification": "MBBS, MD",
        "specialization": "Cardiologist",
        "experience_years": 8,
        "license_number": "LIC-UNAPPROVED",
        "consultation_fee": 1200.0,
        "clinic_address": "Heart Clinic, Mumbai"
    }
    await client.post("/auth/register/doctor", json=payload)

    login_payload = {
        "email": payload["email"],
        "password": payload["password"]
    }
    response = await client.post("/auth/login", json=login_payload)
    assert response.status_code == 403
    assert "pending admin approval" in response.json()["message"].lower()
