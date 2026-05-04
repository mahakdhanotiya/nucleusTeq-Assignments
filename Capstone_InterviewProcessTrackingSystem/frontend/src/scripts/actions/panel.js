import { fetchHandler } from "../lib/handlers/fetch.js";

/**
 * Panel action functions.
 * Centralizes all panel-related API calls.
 */

export async function getAllPanels() {
  return fetchHandler("/api/panels", {
    method: "GET",
    requireAuth: true
  });
}

export async function createPanel(body) {
  return fetchHandler("/api/panels", {
    method: "POST",
    body,
    requireAuth: true
  });
}

export async function updatePanel(id, body) {
  return fetchHandler("/api/panels/" + id, {
    method: "PUT",
    body,
    requireAuth: true
  });
}

export async function deletePanel(id) {
  return fetchHandler("/api/panels/" + id, {
    method: "DELETE",
    requireAuth: true
  });
}
