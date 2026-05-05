import { fetchHandler } from "../lib/handlers/fetch.js";

/**
 * Job action functions.
 * Centralizes all job-related API calls.
 */

export async function getAllJobs() {
  return fetchHandler("/jobs/all", {
    method: "GET",
    requireAuth: true
  });
}

export async function getActiveJobs() {
  return fetchHandler("/jobs", {
    method: "GET",
    requireAuth: true
  });
}

export async function createJob(body) {
  return fetchHandler("/jobs", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function updateJob(id, body) {
  return fetchHandler("/jobs/" + id, {
    method: "PUT",
    body,
    requireAuth: true
  });
}

export async function deactivateJob(id) {
  return fetchHandler("/jobs/" + id + "/deactivate", {
    method: "PUT",
    requireAuth: true
  });
}

export async function activateJob(id) {
  return fetchHandler("/jobs/" + id + "/activate", {
    method: "PUT",
    requireAuth: true
  });
}
