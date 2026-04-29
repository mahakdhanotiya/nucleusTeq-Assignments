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

export async function getFeedbackByInterview(interviewId) {
  return fetchHandler("/api/feedback/interview/" + interviewId, {
    method: "GET",
    requireAuth: true
  });
}
