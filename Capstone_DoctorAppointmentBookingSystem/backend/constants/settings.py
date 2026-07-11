from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Configuration for the Doctor Appointment Booking System, loaded from .env."""

    MONGO_URI: str

    DATABASE_NAME: str

    JWT_SECRET_KEY: str

    JWT_ALGORITHM: str = "HS256"

    JWT_EXPIRY_SECONDS: int = 1800

    APP_NAME: str = "Doctor Appointment Booking System"

    APP_ENV: str = "development"

    ADMIN_SEED_EMAIL: str

    ADMIN_SEED_PASSWORD: str

    ADMIN_SEED_FULL_NAME: str

    ADMIN_SEED_PHONE: str

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )


settings = Settings()