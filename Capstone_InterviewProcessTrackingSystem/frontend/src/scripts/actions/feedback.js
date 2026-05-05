import { fetchHandler } from "../lib/handlers/fetch.js";

/**
 * Feedback action functions.
 * Centralizes all feedback-related API calls.
 */

export async function submitFeedback(body) {
  return fetchHandler("/api/feedback", {
    method: "POST",
    body,
    requireAuth: true
  });
}

/**
 * Fetch feedback for a specific interview.
 * @param {number} interviewId 
 * @param {number} panelId - Optional, used to filter for specific panelist view
 */
export async function getFeedbackByInterview(interviewId, panelId = null) {
  let url = "/api/feedback/interview/" + interviewId;
  if (panelId) url += "?requesterPanelId=" + panelId;
  
  return fetchHandler(url, {
    method: "GET",
    requireAuth: true
  });
}

/**
 * Fetch all feedback for a candidate (HR only).
 * @param {number} candidateId 
 */
export async function getFeedbackByCandidate(candidateId) {
  return fetchHandler("/api/feedback/candidate/" + candidateId, {
    method: "GET",
    requireAuth: true
  });
}
