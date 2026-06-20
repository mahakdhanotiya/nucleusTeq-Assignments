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

    # Application Configuration
    APP_NAME: str = "User Service"
    APP_ENV: str = "development"

    # Load environment variables from .env file
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
    )


# Shared settings instance used across the application
settings = Settings()