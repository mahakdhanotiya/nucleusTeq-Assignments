import { fetchHandler } from "../lib/handlers/fetch.js";

/**
 * Interview action functions.
 * Centralizes all interview-related API calls.
 */

export async function getAllInterviews() {
  return fetchHandler("/api/interviews", {
    method: "GET",
    requireAuth: true
  });
}

export async function scheduleInterview(body) {
  return fetchHandler("/api/interviews", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function deleteInterview(id) {
  return fetchHandler("/api/interviews/" + id, {
    method: "DELETE",
    requireAuth: true
  });
}

export async function assignPanel(body) {
  return fetchHandler("/api/interviews/assign-panel", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function progressStage(body) {
  return fetchHandler("/api/interviews/stage-progression", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function updateInterviewStatus(id, status) {
  return fetchHandler("/api/interviews/" + id + "/status?status=" + status, {
    method: "PUT",
    requireAuth: true
  });
}

export async function updateInterview(id, body) {
  return fetchHandler("/api/interviews/" + id, {
    method: "PUT",
    body,
    requireAuth: true
  });
}
