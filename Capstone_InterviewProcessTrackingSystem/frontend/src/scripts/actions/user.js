import { fetchHandler } from "../lib/handlers/fetch.js";

export async function getMyProfile() {
  return fetchHandler("/candidates/my-profile", {
    method: "GET",
    requireAuth: true
  });
}

export async function updateProfile(body) {
  return fetchHandler("/candidates", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function getJobs() {
  return fetchHandler("/jobs", {
    method: "GET",
    requireAuth: true
  });
}

export async function uploadFile(formData) {
  const token = localStorage.getItem("token");
  const res = await fetch("http://localhost:8080/uploads", {
    method: "POST",
    headers: { "Authorization": "Bearer " + token },
    body: formData
  });
  return await res.json();
}

export async function applyJob(body) {
  return fetchHandler("/candidates", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function getInterviews(candidateId) {
  return fetchHandler(`/api/interviews/candidate/${candidateId}`, {
    method: "GET",
    requireAuth: true
  });
}

export async function getFeedback(candidateId) {
  return fetchHandler(`/api/feedback/candidate/${candidateId}`, {
    method: "GET",
    requireAuth: true
  });
}
