import logging
import time

from fastapi import FastAPI, Request

logger = logging.getLogger(__name__)


def register_logging_middleware(app: FastAPI) -> None:
    """Logs method, path, status code, and processing time for every request."""

    @app.middleware("http")
    async def log_requests(request: Request, call_next):
        start_time = time.perf_counter()
        response = await call_next(request)
        duration_ms = (time.perf_counter() - start_time) * 1000

        logger.info(
            f"{request.method} {request.url.path} "
            f"-> {response.status_code} ({duration_ms:.1f}ms)"
        )
        return response