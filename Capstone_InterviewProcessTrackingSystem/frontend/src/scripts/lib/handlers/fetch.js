import { SITE_CONFIG } from "../../config/site-config.js";

export async function fetchHandler(url, options = {}) {
  const { method = "GET", body, requireAuth = false } = options;

  const headers = {
    "Content-Type": "application/json"
  };

  if (requireAuth) {
    const token = localStorage.getItem("token");
    if (token) {
      headers["Authorization"] = "Bearer " + token;
    }
  }

  const config = { method, headers };
  if (body) {
    config.body = JSON.stringify(body);
  }

  const response = await fetch(SITE_CONFIG.apiUrl + url, config);
  const data = await response.json();
  return data;
}
