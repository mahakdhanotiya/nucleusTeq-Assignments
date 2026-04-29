import { SITE_CONFIG } from "../../config/site-config.js";

/**
 * Centralized fetch handler for all API calls.
 * Handles JSON stringifying, token injection, and response parsing.
 *
 * @param {string} url - API endpoint path (e.g. "/auth/login")
 * @param {Object} options
 * @param {string} options.method - HTTP method (default "GET")
 * @param {Object|FormData} options.body - Request body (Object for JSON, FormData for file uploads)
 * @param {boolean} options.requireAuth - Whether to attach Bearer token (default false)
 * @param {boolean} options.isFormData - Set true when body is FormData (skip JSON headers)
 * @returns {Promise<Object>} Parsed JSON response
 */
export async function fetchHandler(url, options = {}) {
  const { method = "GET", body, requireAuth = false, isFormData = false } = options;

  const headers = {};

  // Only set Content-Type for JSON requests; browser sets it automatically for FormData
  if (!isFormData) {
    headers["Content-Type"] = "application/json";
  }

  if (requireAuth) {
    const token = localStorage.getItem("token");
    if (token) {
      headers["Authorization"] = "Bearer " + token;
    }
  }

  const config = { method, headers };

  if (body) {
    config.body = isFormData ? body : JSON.stringify(body);
  }

  const response = await fetch(SITE_CONFIG.apiUrl + url, config);
  const data = await response.json();
  return data;
}
