import { getMyProfile, getInterviews, getFeedback } from "../actions/user.js";

const token = localStorage.getItem("token");
const userId = localStorage.getItem("userId");

if (!token) {
  alert("Please login first");
  window.location.href = "sign-in/index.html";
}

let cachedProfile = null;

window.showSection = function(n, el) {
  ["dashboard","interviews","feedback"].forEach(s => {
    document.getElementById("sec-"+s).style.display = s === n ? "block" : "none";
  });
  document.querySelectorAll(".sidebar-nav a").forEach(a => a.classList.remove("active"));
  if (el) el.classList.add("active");
  if (n === "interviews") loadInterviewsSection();
  if (n === "feedback") loadFeedbackSection();
};

window.logout = function() {
  localStorage.clear();
  window.location.href = "sign-in/index.html";
};

async function loadProfile() {
  try {
    const data = await getMyProfile();
    if (data.success && data.data) {
      cachedProfile = data.data;
      const p = data.data;
      document.getElementById("profileArea").innerHTML = `
        <h3 style="margin-bottom:16px">Your Application</h3>
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;font-size:.9rem">
          <div><strong>Name:</strong> ${p.fullName || 'N/A'}</div>
          <div><strong>Email:</strong> ${p.email || 'N/A'}</div>
          <div><strong>Mobile:</strong> ${p.mobileNumber || 'N/A'}</div>
          <div><strong>Job Applied:</strong> ${p.jobTitle || 'N/A'}</div>
          <div><strong>Stage:</strong> <span class="badge badge-info">${p.currentStage || 'N/A'}</span></div>
          <div><strong>Status:</strong> <span class="badge ${p.applicationStatus==='SELECTED'?'badge-success':p.applicationStatus==='REJECTED'?'badge-danger':'badge-warning'}">${p.applicationStatus || 'N/A'}</span></div>
          <div><strong>Experience:</strong> ${p.totalExperience || 0} yrs</div>
          <div><strong>Company:</strong> ${p.currentCompany || 'N/A'}</div>
          <div style="grid-column: span 2; margin-top: 12px;">
            ${p.resumeUrl ? `<a href="${p.resumeUrl}" target="_blank" class="btn btn-outline btn-sm" style="display:inline-flex; align-items:center; gap:8px;">
              <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/></svg>
              View Resume (PDF)
            </a>` : '<span style="color:#64748b; font-style:italic;">No resume uploaded</span>'}
          </div>
        </div>`;
      document.getElementById("statsGrid").innerHTML = `
        <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg></div><div><div class="stat-value">${p.currentStage||'--'}</div><div class="stat-label">Current Stage</div></div></div>
        <div class="stat-card"><div class="stat-icon" style="background:#d1fae5"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg></div><div><div class="stat-value">${p.applicationStatus||'--'}</div><div class="stat-label">Status</div></div></div>
        <div class="stat-card"><div class="stat-icon" style="background:#fef3c7"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#f59e0b" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg></div><div><div class="stat-value">${p.totalExperience||0} yrs</div><div class="stat-label">Experience</div></div></div>`;
        
      renderPipeline(p.currentStage);
    } else {
      document.getElementById("profileArea").innerHTML = `
        <div class="empty-state">
          <svg class="empty-svg" viewBox="0 0 24 24"><path d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/></svg>
          <p>You haven't applied for any job yet.</p>
          <a href="index.html" class="btn btn-primary" style="margin-top:12px">Browse Jobs</a>
        </div>`;
      document.getElementById("pipelineArea").innerHTML = `<p class="empty-state">No active application</p>`;
    }
  } catch (e) {
    document.getElementById("profileArea").innerHTML = '<p class="empty-state">Unable to load profile. Check server.</p>';
  }
}

function renderPipeline(currentStage) {
  const stages = ['PROFILING', 'SCREENING', 'L1', 'L2', 'HR'];
  let currentIndex = stages.indexOf(currentStage);
  if (currentIndex === -1) currentIndex = 0; // Default to first if unknown

  // Calculate width for fill line. (currentIndex) / (stages.length - 1)
  const fillPercentage = stages.length > 1 ? (currentIndex / (stages.length - 1)) * 100 : 0;

  const stepsHtml = stages.map((stage, index) => {
    let stateClass = '';
    let icon = index + 1;
    if (index < currentIndex) {
      stateClass = 'completed';
      icon = '✓';
    } else if (index === currentIndex) {
      stateClass = 'current';
    }
    return `
      <div class="pipeline-step ${stateClass}">
        <div class="pipeline-circle">${icon}</div>
        <div class="pipeline-label">${stage.replace('_', ' ')}</div>
      </div>
    `;
  }).join('');

  document.getElementById("pipelineArea").innerHTML = `
    <div class="pipeline-line"></div>
    <div class="pipeline-line-fill" style="width: ${fillPercentage}%; background: #10b981;"></div>
    ${stepsHtml}
  `;
}

window.openEditProfileModal = function() {
  if (!cachedProfile) {
    toast("No profile found. Apply for a job first.", "error");
    return;
  }
  document.getElementById("epMobile").value = cachedProfile.mobileNumber || '';
  document.getElementById("epExperience").value = cachedProfile.totalExperience || 0;
  document.getElementById("epCompany").value = cachedProfile.currentCompany || '';
  document.getElementById("epResume").value = cachedProfile.resumeUrl || '';
  document.getElementById("epCurrentCTC").value = cachedProfile.currentCTC || '';
  document.getElementById("epExpectedCTC").value = cachedProfile.expectedCTC || '';
  document.getElementById("epNotice").value = cachedProfile.noticePeriod || 0;
  document.getElementById("epLocation").value = cachedProfile.preferredLocation || '';
  document.getElementById("epMsg").textContent = "";
  
  document.getElementById("editProfileModal").classList.add("show");
};

window.closeModal = function(id) {
  document.getElementById(id).classList.remove("show");
};

window.handleEditProfile = async function(e) {
  e.preventDefault();
  const body = {
    mobileNumber: document.getElementById("epMobile").value,
    totalExperience: parseInt(document.getElementById("epExperience").value),
    currentCompany: document.getElementById("epCompany").value,
    resumeUrl: document.getElementById("epResume").value,
    currentCTC: parseFloat(document.getElementById("epCurrentCTC").value) || null,
    expectedCTC: parseFloat(document.getElementById("epExpectedCTC").value) || null,
    noticePeriod: parseInt(document.getElementById("epNotice").value) || 0,
    preferredLocation: document.getElementById("epLocation").value
  };

  const btn = e.target.querySelector('button[type="submit"]');
  const prevText = btn.textContent;
  btn.textContent = "Saving...";
  btn.disabled = true;

  try {
    const res = await fetch("http://localhost:8080/api/candidates/update", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      },
      body: JSON.stringify(body)
    });
    const data = await res.json();
    if (data.success) {
      toast("Profile updated successfully!");
      closeModal("editProfileModal");
      loadProfile(); // reload
    } else {
      document.getElementById("epMsg").className = "msg error";
      document.getElementById("epMsg").textContent = data.message || "Failed to update profile";
    }
  } catch (error) {
    document.getElementById("epMsg").className = "msg error";
    document.getElementById("epMsg").textContent = "Server error. Please try again later.";
  } finally {
    btn.textContent = prevText;
    btn.disabled = false;
  }
};

function toast(msg, type="success") {
  const container = document.getElementById("toastContainer");
  if(!container) return alert(msg);
  const t = document.createElement("div");
  t.className = `toast toast-${type}`;
  t.textContent = msg;
  container.appendChild(t);
  setTimeout(() => { t.style.opacity="0"; setTimeout(()=>t.remove(), 300); }, 3000);
}

async function loadInterviewsSection() {
  if (!cachedProfile) return;
  try {
    const data = await getInterviews(cachedProfile.id);
    const list = data.data || [];
    const body = document.getElementById("interviewsBody");
    if (!list.length) { body.innerHTML = '<tr><td colspan="6" class="empty-state">No interviews scheduled yet.</td></tr>'; return; }
    body.innerHTML = list.map(i => `<tr>
      <td><strong>I-${i.id}</strong></td>
      <td>${i.jobTitle || 'N/A'}</td>
      <td><span class="badge badge-info">${i.stage || 'N/A'}</span></td>
      <td>${i.interviewDateTime ? new Date(i.interviewDateTime).toLocaleString() : 'TBD'}</td>
      <td>${i.focusArea || '--'}</td>
      <td><span class="badge badge-warning">${i.status || 'SCHEDULED'}</span></td>
    </tr>`).join("");
  } catch (e) { document.getElementById("interviewsBody").innerHTML = '<tr><td colspan="6" class="empty-state">Error loading interviews.</td></tr>'; }
}

async function loadFeedbackSection() {
  if (!cachedProfile) { document.getElementById("feedbackArea").innerHTML = '<p class="empty-state">No profile found.</p>'; return; }
  try {
    const data = await getFeedback(cachedProfile.id);
    const list = data.data || [];
    if (!list.length) { document.getElementById("feedbackArea").innerHTML = '<div class="empty-state"><svg class="empty-svg" viewBox="0 0 24 24"><path d="M20 2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4V4c0-1.1-.9-2-2-2z"/></svg><p>No feedback available yet.</p></div>'; return; }
    document.getElementById("feedbackArea").innerHTML = list.map(f => `
      <div class="feedback-card">
        <p><strong>Stage:</strong> <span class="badge badge-info">${f.stage || 'N/A'}</span></p>
        <p><strong>Panel:</strong> ${f.panelName || 'N/A'}</p>
        <p class="rating"><strong>Rating:</strong> ${'★'.repeat(f.rating || 0)}${'☆'.repeat(5 - (f.rating || 0))}</p>
        <p><strong>Decision:</strong> <span class="${f.status === 'SELECTED' ? 'decision-selected' : 'decision-rejected'}">${f.status === 'SELECTED' ? 'Selected' : 'Rejected'}</span></p>
        <p><strong>Comments:</strong> ${f.comments || '--'}</p>
        <p><strong>Strengths:</strong> ${f.strengths || '--'}</p>
        <p><strong>Weaknesses:</strong> ${f.weaknesses || '--'}</p>
      </div>`).join("");
  } catch (e) { document.getElementById("feedbackArea").innerHTML = '<p class="empty-state">Error loading feedback.</p>'; }
}

loadProfile();
