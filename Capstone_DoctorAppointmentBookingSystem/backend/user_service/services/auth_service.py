import logging

from models.user import User
from models.doctor_profile import DoctorProfile
from models.patient_profile import PatientProfile
from enums.user_role import UserRole
from schemas.request.auth_request import RegisterRequest, LoginRequest
from schemas.response.auth_response import RegisterResponse, TokenResponse, UserSummaryResponse
from utils.password import hash_password, verify_password
from utils.jwt_handler import create_access_token
from repositories.user_repository import get_user_by_email, create_user
from repositories.doctor_repository import get_doctor_by_license_number, create_doctor_profile
from repositories.patient_repository import create_patient_profile
from exceptions.auth_exceptions import (
    DuplicateEmailError,
    DuplicateLicenseNumberError,
    InvalidCredentialsError,
    AccountDeactivatedError,
)

logger = logging.getLogger(__name__)


async def register_user(request: RegisterRequest) -> RegisterResponse:
    """Registers a new Patient or Doctor account and creates the matching profile."""
    if await get_user_by_email(request.email) is not None:
        raise DuplicateEmailError(email=request.email)

    if request.role == UserRole.DOCTOR:
        if await get_doctor_by_license_number(request.license_number) is not None:
            raise DuplicateLicenseNumberError(license_number=request.license_number)

    new_user = User(
        full_name=request.full_name,
        email=request.email,
        password_hash=hash_password(request.password),
        phone_number=request.phone_number,
        role=request.role,
    )
    await create_user(new_user)

    if request.role == UserRole.PATIENT:
        await create_patient_profile(
            PatientProfile(
                user_id=new_user.id,
                gender=request.gender,
                date_of_birth=request.date_of_birth,
            )
        )

    if request.role == UserRole.DOCTOR:
        await create_doctor_profile(
            DoctorProfile(
                user_id=new_user.id,
                qualification=request.qualification,
                specialization=request.specialization,
                experience_years=request.experience_years,
                license_number=request.license_number,
            )
        )

    logger.info(f"New user registered: {new_user.email} (role={new_user.role.value})")

    return RegisterResponse(
        user_id=str(new_user.id),
        email=new_user.email,
        role=new_user.role,
    )


async def login_user(request: LoginRequest) -> TokenResponse:
    """Authenticates a user and issues a JWT access token."""
    user = await get_user_by_email(request.email)
    if user is None:
        raise InvalidCredentialsError()

    if not verify_password(request.password, user.password_hash):
        raise InvalidCredentialsError()

    if not user.is_active:
        raise AccountDeactivatedError()

    token, expires_in = create_access_token(
        user_id=str(user.id),
        email=user.email,
        role=user.role.value,
    )

    logger.info(f"User logged in: {user.email} (role={user.role.value})")

    return TokenResponse(
        access_token=token,
        expires_in=expires_in,
        user=UserSummaryResponse(
            id=str(user.id),
            full_name=user.full_name,
            email=user.email,
            role=user.role,
        ),
    )