from fastapi import APIRouter, status

from schemas.request.auth_request import PatientRegisterRequest, DoctorRegisterRequest, LoginRequest
from schemas.response.auth_response import PatientRegisterResponse, DoctorRegisterResponse, TokenResponse
from services.auth_service import register_patient, register_doctor, login_user

router = APIRouter(prefix="/auth", tags=["Authentication"])


@router.post("/register/patient", response_model=PatientRegisterResponse, status_code=status.HTTP_201_CREATED)
async def register_patient_endpoint(request: PatientRegisterRequest) -> PatientRegisterResponse:
    """Registers a new Patient account and creates a patient profile."""
    return await register_patient(request)


@router.post("/register/doctor", response_model=DoctorRegisterResponse, status_code=status.HTTP_201_CREATED)
async def register_doctor_endpoint(request: DoctorRegisterRequest) -> DoctorRegisterResponse:
    """Registers a new Doctor account and creates a doctor profile."""
    return await register_doctor(request)


@router.post("/login", response_model=TokenResponse, status_code=status.HTTP_200_OK)
async def login(request: LoginRequest) -> TokenResponse:
    """Authenticates a user and returns a JWT access token."""
    return await login_user(request)