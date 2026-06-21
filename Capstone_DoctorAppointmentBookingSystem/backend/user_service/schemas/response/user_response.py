from datetime import datetime

from pydantic import BaseModel

from enums.user_role import UserRole


class UserProfileResponse(BaseModel):
    """Response body for GET /users/me and PUT /users/me."""

    id: str
    full_name: str
    email: str
    phone_number: str
    role: UserRole
    is_active: bool
    created_at: datetime
    updated_at: datetime
    
class MessageResponse(BaseModel):
    """Generic confirmation response for actions with no resource to return."""
 
    success: bool = True
    message: str
 