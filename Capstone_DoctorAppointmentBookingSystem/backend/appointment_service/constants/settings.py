# Loads all configuration values from the .env file using pydantic-settings.
# Import the `settings` object anywhere in the project to access config.
# The app will refuse to start if any required variable is missing from .env.

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """All configuration for the Appointment Service, loaded from .env."""

    # --- MongoDB ---
    MONGO_URI: str
    DATABASE_NAME: str

    # --- JWT ---
    # Used for VERIFICATION only. Appointment Service never issues tokens.
    # Must match the JWT_SECRET_KEY value in User Service.
    JWT_SECRET_KEY: str
    JWT_ALGORITHM: str = "HS256"

    # --- User Service integration ---
    USER_SERVICE_BASE_URL: str
    INTERNAL_API_KEY: str

    # --- App ---
    APP_NAME: str = "Appointment Service"
    APP_ENV: str = "development"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",  # Ignore any extra variables in .env
    )


# Single shared instance — import this object, not the class.
settings = Settings()