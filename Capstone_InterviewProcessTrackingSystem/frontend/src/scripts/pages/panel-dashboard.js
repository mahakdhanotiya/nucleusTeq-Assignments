import { getAllInterviews } from "../actions/interview.js";
import { submitFeedback as submitFeedbackAction, getFeedbackByInterview } from "../actions/feedback.js";
import { renderSidebarProfile, showFieldError, clearErrors, getTrimmedValues, initFormCleanup } from "../lib/utils/ui.js";

const token = localStorage.getItem("token");
if (!token || localStorage.getItem("role") !== "PANEL") {
  window.location.href = "sign-in/index.html";
}

let cachedAssigned = [];

/**
 * Displays a temporary toast message to the user.
 * @param {string} msg - The message to display.
 * @param {string} type - Toast type ('success' or 'error').
 */
window.toast = function(msg, type="success") {
  const c = document.getElementById("toastContainer");
  const t = document.createElement("div");
  t.className = "toast " + type;
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}

/**
 * Logs out the user and clears local storage.
 */
window.logout = function() {
  localStorage.clear();
  window.location.href = "sign-in/index.html";
}

window.openModal = function(id) { document.getElementById(id).classList.add("active"); }
window.closeModal = function(id) { document.getElementById(id).classList.remove("active"); }

/**
 * Switches the dashboard to a specific section.
 * @param {string} name - Section ID suffix.
 * @param {HTMLElement} el - Clicked sidebar element.
 */
window.showSec = function(name, el) {
  ["assigned", "feedback"].forEach(s => 
    document.getElementById("sec-" + s).style.display = (s === name) ? "block" : "none"
  );
  document.querySelectorAll(".sidebar-nav a").forEach(a => a.classList.remove("active"));
  if (el) el.classList.add("active");
}

/**
 * Fetches and renders interviews assigned to the logged-in panelist.
 */
async function loadAssigned() {
  try {
    const data = await getAllInterviews();
    const list = data.data || [];
    const panelId = parseInt(localStorage.getItem("profileId"));
    
    // Filter interviews to only show those where the current panelist is assigned
    cachedAssigned = list.filter(i => i.assignedPanelIds && i.assignedPanelIds.includes(panelId));
    renderAssigned(cachedAssigned);
  } catch (e) {
    document.getElementById("assignedBody").innerHTML = '<tr><td colspan="7" class="empty-state">Error loading interviews.</td></tr>';
  }
}

function renderAssigned(list) {
    const body = document.getElementById("assignedBody");
    if (!list.length) {
      body.innerHTML = '<tr><td colspan="7" class="empty-state">No interviews found.</td></tr>';
      return;
    }
    const panelId = parseInt(localStorage.getItem("profileId"));

    body.innerHTML = list.map(i => {
      const alreadySubmitted = i.feedbackProvidedBy && i.feedbackProvidedBy.includes(panelId);
      const isAssigned = i.assignedPanelIds && i.assignedPanelIds.includes(panelId);
      
      const interviewTime = new Date(i.interviewDateTime);
      const now = new Date();
      const isTimeLocked = now < interviewTime;

      let actionHtml = '';
      if (alreadySubmitted) {
        actionHtml = `<button class="btn btn-sm btn-info" onclick="viewMyFeedback(${i.id})">View My Feedback</button>`;
      } else if (isAssigned && i.status !== 'CANCELLED' && i.status !== 'NO_SHOW') {
        if (isTimeLocked) {
          actionHtml = `<button class="btn btn-sm btn-secondary" disabled title="Starts at ${interviewTime.toLocaleTimeString()}">⏰ Locked</button><br><small style="color:#64748b;font-size:10px">Available at ${interviewTime.toLocaleTimeString()}</small>`;
        } else {
          actionHtml = `<button class="btn btn-sm btn-primary" onclick="prefillFeedback(${i.id}, '${i.candidateName}', ${i.candidateId})">Give Feedback</button>`;
        }
      } else {
        actionHtml = '--';
      }

      return `<tr>
        <td><strong>I-${i.id}</strong></td>
        <td><strong>${i.candidateName || 'N/A'}</strong><br>
            <small style="color:#64748b">C-${i.candidateId}</small><br>
            <a href="${i.candidateResumeUrl}" target="_blank" style="font-size:11px;color:#4f46e5;text-decoration:none;font-weight:600">📄 View Resume</a>
        </td>
        <td>${i.jobTitle || 'N/A'}</td>
        <td><span class="badge badge-info">${i.stage || 'N/A'}</span></td>
        <td>${i.interviewDateTime ? interviewTime.toLocaleString() : 'TBD'}</td>
        <td>${i.focusArea || '--'}</td>
        <td>${actionHtml}</td>
      </tr>`;
    }).join("");
}

// Search Listener
document.getElementById("searchAssigned")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedAssigned.filter(i => 
        (i.candidateName || '').toLowerCase().includes(q) || 
        (i.jobTitle || '').toLowerCase().includes(q) ||
        i.id.toString().includes(q)
    );
    renderAssigned(filtered);
});

/**
 * Navigates to the feedback form and prefills interview details.
 * @param {number} id - Interview ID.
 * @param {string} name - Candidate name.
 * @param {number} candId - Candidate ID.
 */
window.prefillFeedback = function(id, name, candId) {
  document.getElementById("fbIntId").value = id;
  // Update header or info area if it exists
  const formHeader = document.querySelector("#sec-feedback h1");
  if (formHeader) formHeader.textContent = `Submit Feedback for ${name} (C-${candId})`;
  
  showSec("feedback", document.querySelectorAll(".sidebar-nav a")[1]);
}

window.submitFeedback = async function(e) {
  e.preventDefault();
  clearErrors("feedbackForm");
  const vals = getTrimmedValues("feedbackForm");
  const msgEl = document.getElementById("fbMsg"); 
  msgEl.style.display = "none";

  let hasError = false;
  if (!vals.fbRating) { showFieldError("fbRating", "Rating is required"); hasError = true; }
  else if (parseInt(vals.fbRating) < 1 || parseInt(vals.fbRating) > 5) { showFieldError("fbRating", "Rating must be 1-5"); hasError = true; }
  
  if (!vals.fbComments) { showFieldError("fbComments", "Comments are required"); hasError = true; }

  if (hasError) return;

  const body = {
    interviewId: parseInt(vals.fbIntId),
    panelId: parseInt(localStorage.getItem("profileId")),
    rating: parseInt(vals.fbRating),
    status: vals.fbStatus,
    comments: vals.fbComments,
    strengths: vals.fbStrengths,
    weaknesses: vals.fbWeaknesses,
    areasCovered: vals.fbAreas
  };
  
  try {
    const data = await submitFeedbackAction(body);
    if (data.success) {
      toast("Feedback submitted!");
      document.getElementById("feedbackForm").reset();
      loadAssigned(); 
      showSec("assigned", document.querySelectorAll(".sidebar-nav a")[0]);
    } else {
      msgEl.className = "msg error";
      msgEl.textContent = data.message || "Failed to submit feedback";
      msgEl.style.display = "block";
    }
  } catch(e) {
    msgEl.className = "msg error";
    msgEl.textContent = "Server error";
    msgEl.style.display = "block";
  }
}

/**
 * Fetches and displays the panelist's own feedback for a specific interview.
 * @param {number} intId - Interview ID.
 */
window.viewMyFeedback = async function(intId) {
  const contentEl = document.getElementById("feedbackContent");
  contentEl.innerHTML = "<p class='empty-state'>Loading your feedback...</p>";
  window.openModal("feedbackModal");

  try {
    const panelId = parseInt(localStorage.getItem("profileId"));
    const data = await getFeedbackByInterview(intId, panelId);
    
    if (!data.success || !data.data || data.data.length === 0) {
      contentEl.innerHTML = "<p class='empty-state'>No feedback found.</p>";
      return;
    }

    contentEl.innerHTML = data.data.map(f => {
      const rating = f.rating || 0;
      const decisionClass = f.status === 'SELECTED' ? 'decision-selected' : 'decision-rejected';
      return `
      <div class="evaluation-card" style="margin-bottom: 16px; box-shadow: none; border: 1px solid #e2e8f0;">
        <div class="eval-header" style="padding: 14px 18px;">
          <div class="eval-candidate-info">
            <h4 style="font-size: 1rem; margin: 0;">My Evaluation</h4>
            <p style="margin: 0; font-size: 0.75rem; color: #64748b;">Feedback #F-${f.id}</p>
          </div>
          <div class="eval-stars" style="font-size: 1.1rem;">${'★'.repeat(rating)}${'☆'.repeat(5-rating)}</div>
        </div>
        <div class="eval-body" style="padding: 16px;">
          <div class="eval-section">
            <span class="eval-section-label">Strengths</span>
            <div class="eval-text-box eval-box-strengths" style="font-size: 0.85rem; padding: 10px;">${f.strengths || 'N/A'}</div>
          </div>
          <div class="eval-section">
            <span class="eval-section-label">Weaknesses</span>
            <div class="eval-text-box eval-box-weaknesses" style="font-size: 0.85rem; padding: 10px;">${f.weaknesses || 'N/A'}</div>
          </div>
          <div class="eval-section" style="margin-bottom: 0;">
            <span class="eval-section-label">Comments</span>
            <div class="eval-text-box eval-box-comments" style="font-size: 0.85rem; padding: 10px;">${f.comments || 'N/A'}</div>
          </div>
        </div>
        <div class="eval-footer" style="padding: 12px 18px;">
          <span class="decision-badge ${decisionClass}">${f.status}</span>
          <span style="font-size: 0.75rem; color: #94a3b8;">${f.areasCovered ? 'Topics: ' + f.areasCovered : ''}</span>
        </div>
      </div>`;
    }).join("");
  } catch(err) {
    console.error(err);
    contentEl.innerHTML = "<p class='msg error'>Failed to load feedback.</p>";
  }
}

document.addEventListener("DOMContentLoaded", () => {
  renderSidebarProfile();
  loadAssigned();
  initFormCleanup("feedbackForm");
});
