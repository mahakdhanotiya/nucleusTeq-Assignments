import { getAllInterviews } from "../actions/interview.js";
import { submitFeedback as submitFeedbackAction } from "../actions/feedback.js";

const token = localStorage.getItem("token");
if (!token || localStorage.getItem("role") !== "PANEL") {
  window.location.href = "sign-in/index.html";
}

window.toast = function(msg, type="success") {
  const c = document.getElementById("toastContainer");
  const t = document.createElement("div");
  t.className = "toast " + type;
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}

window.logout = function() {
  localStorage.clear();
  window.location.href = "sign-in/index.html";
}

window.showSec = function(name, el) {
  ["assigned", "feedback"].forEach(s => 
    document.getElementById("sec-" + s).style.display = (s === name) ? "block" : "none"
  );
  document.querySelectorAll(".sidebar-nav a").forEach(a => a.classList.remove("active"));
  if (el) el.classList.add("active");
}

async function loadAssigned() {
  try {
    const data = await getAllInterviews();
    const list = data.data || [];
    const body = document.getElementById("assignedBody");
    if (!list.length) {
      body.innerHTML = '<tr><td colspan="7" class="empty-state">No interviews assigned.</td></tr>';
      return;
    }
    body.innerHTML = list.map(i => `<tr>
      <td><strong>I-${i.id}</strong></td>
      <td>${i.candidateName || 'N/A'}</td>
      <td>${i.jobTitle || 'N/A'}</td>
      <td><span class="badge badge-info">${i.stage || 'N/A'}</span></td>
      <td>${i.interviewDateTime ? new Date(i.interviewDateTime).toLocaleString() : 'TBD'}</td>
      <td>${i.focusArea || '--'}</td>
      <td><button class="btn btn-sm btn-primary" onclick="prefillFeedback(${i.id})">Give Feedback</button></td>
    </tr>`).join("");
  } catch (e) {
    document.getElementById("assignedBody").innerHTML = '<tr><td colspan="7" class="empty-state">Error loading interviews.</td></tr>';
  }
}

window.prefillFeedback = function(id) {
  document.getElementById("fbIntId").value = id;
  showSec("feedback", document.querySelectorAll(".sidebar-nav a")[1]);
}

window.submitFeedback = async function(e) {
  e.preventDefault();
  const msgEl = document.getElementById("fbMsg"); 
  msgEl.style.display = "none";
  const body = {
    interviewId: parseInt(document.getElementById("fbIntId").value),
    panelId: null,
    rating: parseInt(document.getElementById("fbRating").value),
    status: document.getElementById("fbStatus").value,
    comments: document.getElementById("fbComments").value,
    strengths: document.getElementById("fbStrengths").value,
    weaknesses: document.getElementById("fbWeaknesses").value,
    areasCovered: document.getElementById("fbAreas").value
  };
  
  try {
    const data = await submitFeedbackAction(body);
    if (data.success) {
      toast("Feedback submitted!");
      document.getElementById("feedbackForm").reset();
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

document.addEventListener("DOMContentLoaded", () => {
  loadAssigned();
});
