import logging
import time

from fastapi import FastAPI, Request

logger = logging.getLogger(__name__)


def register_logging_middleware(app: FastAPI) -> None:
    """Attaches the request logging middleware to the FastAPI application."""

    @app.middleware("http")
    async def log_requests(request: Request, call_next):
        start = time.perf_counter()
        response = await call_next(request)
        duration_ms = (time.perf_counter() - start) * 1000

        logger.info(
            f"{request.method} {request.url.path} "
            f"→ {response.status_code} ({duration_ms:.1f}ms)"
        )
        return response