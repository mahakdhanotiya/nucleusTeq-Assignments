from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    """Monolithic configuration for the Doctor Appointment Booking System, loaded from .env."""

    # --- MongoDB ---
    MONGO_URI: str
    DATABASE_NAME: str

    # --- JWT ---
    JWT_SECRET_KEY: str
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRY_MINUTES: int = 30

    # --- Legacy Service Integration ---
    USER_SERVICE_BASE_URL: str = "http://127.0.0.1:8001"
    INTERNAL_API_KEY: str = ""

    # --- App ---
    APP_NAME: str = "Doctor Appointment Booking System"
    APP_ENV: str = "development"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

settings = Settings()
