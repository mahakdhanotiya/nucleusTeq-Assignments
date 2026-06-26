from fastapi import APIRouter, status

from schemas.request.auth_request import RegisterRequest, LoginRequest
from schemas.response.auth_response import RegisterResponse, TokenResponse
from services.auth_service import register_user, login_user

router = APIRouter(prefix="/auth", tags=["Authentication"])


@router.post("/register", response_model=RegisterResponse, status_code=status.HTTP_201_CREATED)
async def register(request: RegisterRequest) -> RegisterResponse:
    """Registers a new Patient or Doctor account."""
    return await register_user(request)


@router.post("/login", response_model=TokenResponse, status_code=status.HTTP_200_OK)
async def login(request: LoginRequest) -> TokenResponse:
    """Authenticates a user and returns a JWT access token."""
    return await login_user(request)