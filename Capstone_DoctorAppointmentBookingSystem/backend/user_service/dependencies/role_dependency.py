from fastapi import Depends

from models.user import User
from enums.user_role import UserRole
from dependencies.auth_dependency import get_current_user
from exceptions.user_exceptions import UnauthorizedError


def require_role(allowed_role: UserRole):
    """Returns a dependency that enforces the current user has the given role."""

    async def dependency(user: User = Depends(get_current_user)) -> User:
        if user.role != allowed_role:
            raise UnauthorizedError(required_role=allowed_role.value)
        return user

    return dependency


require_admin = require_role(UserRole.ADMIN)
require_doctor = require_role(UserRole.DOCTOR)
require_patient = require_role(UserRole.PATIENT)