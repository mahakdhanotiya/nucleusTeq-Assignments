import { fetchHandler } from "../lib/handlers/fetch.js";

/**
 * Candidate action functions.
 * Centralizes all candidate-related API calls.
 */

export async function getAllCandidates() {
  return fetchHandler("/candidates", {
    method: "GET",
    requireAuth: true
  });
}

export async function deleteCandidate(id) {
  return fetchHandler("/candidates/" + id, {
    method: "DELETE",
    requireAuth: true
  });
}

export async function uploadFile(formData) {
  return fetchHandler("/api/files/upload", {
    method: "POST",
    body: formData,
    requireAuth: true,
    isFormData: true
  });
}

export async function createCandidate(body) {
  return fetchHandler("/candidates", {
    method: "POST",
    body: body,
    requireAuth: true
  });
}

export async function getMyProfile() {
  return fetchHandler("/candidates/my-profile", {
    method: "GET",
    requireAuth: true
  });
}

export async function updateMyProfile(body) {
  return fetchHandler("/candidates/update", {
    method: "PUT",
    body: body,
    requireAuth: true
  });
}

/**
 * HR: Search/filter candidates by JD, stage, and/or status.
 */
export async function searchCandidates(jdId, stage, status, name) {
  let query = "";
  const params = [];
  if (jdId) params.push(`jdId=${jdId}`);
  if (stage) params.push(`stage=${stage}`);
  if (status) params.push(`status=${status}`);
  if (name) params.push(`name=${encodeURIComponent(name)}`);
  
  if (params.length > 0) {
    query = "?" + params.join("&");
  }
  
  return fetchHandler("/candidates/search" + query, {
    method: "GET",
    requireAuth: true
  });
}
