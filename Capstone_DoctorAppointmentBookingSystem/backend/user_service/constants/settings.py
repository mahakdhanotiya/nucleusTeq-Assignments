from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application settings loaded from .env file."""

    # MongoDB Configuration
    MONGO_URI: str
    DATABASE_NAME: str

    # JWT Configuration
    JWT_SECRET_KEY: str
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRY_MINUTES: int = 30
    
    # Shared secret used to authenticate internal service-to-service calls.
    # Appointment Service must send this value in the X-Internal-Key header.
    # Generate with: python -c "import secrets; print(secrets.token_hex(32))"
    INTERNAL_API_KEY: str

    # Application Configuration
    APP_NAME: str = "User Service"
    APP_ENV: str = "development"

    # Load environment variables from .env file
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )


# Shared settings instance used across the application
settings = Settings()