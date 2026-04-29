import { getMyProfile, getInterviews, getJobs } from "../actions/user.js";

const token = localStorage.getItem("token");
const userId = localStorage.getItem("userId");

if (!token) {
  alert("Please login first");
  window.location.href = "sign-in/index.html";
}

let cachedProfile = null;

function formatStatus(str) {
  if (!str) return 'N/A';
  return str.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
}

window.viewResume = function(url) {
  if (!url) {
    toast('No resume available', 'error');
    return;
  }
  window.open(url, '_blank');
};

window.showSection = function(n, el) {
  ["dashboard","interviews","browse-jobs"].forEach(s => {
    document.getElementById("sec-"+s).style.display = s === n ? "block" : "none";
  });
  document.querySelectorAll(".sidebar-nav a").forEach(a => a.classList.remove("active"));
  if (el) el.classList.add("active");
  if (n === "interviews") loadInterviewsSection();
  if (n === "browse-jobs") loadBrowseJobsSection();
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
          <div><strong>Stage:</strong> <span class="badge badge-info">${formatStatus(p.currentStage)}</span></div>
          <div><strong>Status:</strong> <span class="badge ${p.applicationStatus==='SELECTED'?'badge-success':p.applicationStatus==='REJECTED'?'badge-danger':'badge-warning'}">${formatStatus(p.applicationStatus)}</span></div>
          <div><strong>Experience:</strong> ${p.totalExperience || 0} yrs</div>
          <div><strong>Company:</strong> ${p.currentCompany || 'N/A'}</div>
          <div style="grid-column: span 2; margin-top: 12px;">
            ${p.resumeUrl ? `<button onclick="viewResume('${p.resumeUrl}')" class="btn btn-outline btn-sm" style="display:inline-flex; align-items:center; gap:8px; cursor:pointer;">
              <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/></svg>
              View Resume (PDF)
            </button>` : '<span style="color:#64748b; font-style:italic;">No resume uploaded</span>'}
          </div>
        </div>`;
      document.getElementById("statsGrid").innerHTML = `
        <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg></div><div><div class="stat-value">${formatStatus(p.currentStage)}</div><div class="stat-label">Current Stage</div></div></div>
        <div class="stat-card"><div class="stat-icon" style="background:#d1fae5"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg></div><div><div class="stat-value">${formatStatus(p.applicationStatus)}</div><div class="stat-label">Status</div></div></div>
        <div class="stat-card"><div class="stat-icon" style="background:#fef3c7"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#f59e0b" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg></div><div><div class="stat-value">${p.totalExperience||0} yrs</div><div class="stat-label">Experience</div></div></div>`;
        
      renderPipeline(p.currentStage);
      // Show Edit Profile button only when profile exists
      document.getElementById("editProfileBtn").style.display = "inline-flex";
    } else {
      document.getElementById("profileArea").innerHTML = `
        <div class="empty-state">
          <svg class="empty-svg" viewBox="0 0 24 24"><path d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/></svg>
          <p>You haven't applied for any job yet.</p>
          <a href="#" onclick="showSection('browse-jobs', document.querySelectorAll('.sidebar-nav a')[2])" class="btn btn-primary" style="margin-top:12px">Browse Jobs</a>
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
  document.getElementById("epRelExp").value = cachedProfile.relevantExperience || 0;
  document.getElementById("epCompany").value = cachedProfile.currentCompany || '';
  document.getElementById("epResumeFile").value = '';
  const resumeStatus = document.getElementById("epResumeStatus");
  if (cachedProfile.resumeUrl) {
    const fileName = cachedProfile.resumeUrl.split('/').pop();
    resumeStatus.innerHTML = '✅ Current: <strong>' + fileName + '</strong> (select a new file to replace)';
  } else {
    resumeStatus.textContent = 'No resume uploaded yet.';
  }
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
  const btn = e.target.querySelector('button[type="submit"]');
  const prevText = btn.textContent;
  btn.textContent = "Saving...";
  btn.disabled = true;

  let resumeUrl = cachedProfile.resumeUrl || '';

  // Handle file upload if a new file was selected
  const fileInput = document.getElementById("epResumeFile");
  if (fileInput.files && fileInput.files.length > 0) {
    const file = fileInput.files[0];
    // Validate PDF
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      document.getElementById("epMsg").className = "msg error";
      document.getElementById("epMsg").textContent = "Only PDF files are allowed for resume.";
      btn.textContent = prevText;
      btn.disabled = false;
      return;
    }
    // Upload file
    try {
      const formData = new FormData();
      formData.append('file', file);
      const uploadRes = await fetch("http://localhost:8080/api/files/upload", {
        method: "POST",
        headers: { "Authorization": "Bearer " + token },
        body: formData
      });
      const uploadData = await uploadRes.json();
      if (uploadData.success && uploadData.data) {
        resumeUrl = uploadData.data.url;
      } else {
        document.getElementById("epMsg").className = "msg error";
        document.getElementById("epMsg").textContent = uploadData.message || "Failed to upload resume.";
        btn.textContent = prevText;
        btn.disabled = false;
        return;
      }
    } catch (err) {
      document.getElementById("epMsg").className = "msg error";
      document.getElementById("epMsg").textContent = "Error uploading resume. Please try again.";
      btn.textContent = prevText;
      btn.disabled = false;
      return;
    }
  }

  const body = {
    mobileNumber: document.getElementById("epMobile").value,
    totalExperience: parseInt(document.getElementById("epExperience").value),
    relevantExperience: parseInt(document.getElementById("epRelExp").value) || 0,
    currentCompany: document.getElementById("epCompany").value,
    resumeUrl: resumeUrl,
    currentCTC: parseFloat(document.getElementById("epCurrentCTC").value) || null,
    expectedCTC: parseFloat(document.getElementById("epExpectedCTC").value) || null,
    noticePeriod: parseInt(document.getElementById("epNotice").value) || 0,
    preferredLocation: document.getElementById("epLocation").value
  };

  try {
    const res = await fetch("http://localhost:8080/candidates/update", {
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

async function loadBrowseJobsSection() {
  const grid = document.getElementById("browseJobsGrid");
  try {
    const data = await getJobs();
    const jobs = data.data || [];
    if (!jobs.length) {
      grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
          <svg class="empty-svg" viewBox="0 0 24 24"><path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>
          <p>No active jobs available right now. Check back later!</p>
        </div>`;
      return;
    }
    grid.innerHTML = jobs.map(j => `
      <div class="job-card">
        <div class="job-header">
          <h3 class="job-title">${j.title}</h3>
          <span class="job-id">J-${j.id}</span>
        </div>
        <div class="job-detail">
          <svg viewBox="0 0 24 24"><path fill="#64748b" d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/></svg>
          ${j.location || 'Remote'}
        </div>
        <div class="job-detail">
          <svg viewBox="0 0 24 24"><path fill="#64748b" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>
          ${j.minExperience || 0} - ${j.maxExperience || 0} yrs experience
        </div>
        ${j.description ? '<p class="job-desc" style="font-size:0.85rem;color:#64748b;margin:8px 0;">' + j.description + '</p>' : ''}
        ${j.skills && j.skills.length ? '<div class="job-skills" style="display:flex;flex-wrap:wrap;gap:6px;margin:8px 0;">' + j.skills.map(s => '<span class="skill-tag" style="background:#e0e7ff;color:#4f46e5;padding:2px 10px;border-radius:6px;font-size:0.75rem;font-weight:600;">' + s + '</span>').join('') + '</div>' : ''}
        ${j.minSalary ? '<div class="job-salary" style="font-size:0.85rem;color:#059669;font-weight:600;margin:6px 0;">₹ ' + j.minSalary + ' - ' + j.maxSalary + ' LPA</div>' : ''}
        <span class="job-type-badge">${(j.jobType || 'FULL_TIME').replace(/_/g, ' ')}</span>
        <button onclick="window.location.href='candidate-profile.html?jobId=${j.id}&jobTitle=${encodeURIComponent(j.title)}'" class="btn btn-primary btn-sm" style="margin-top:auto;width:100%;justify-content:center;">Apply Now</button>
      </div>
    `).join("");
  } catch (e) {
    grid.innerHTML = `
      <div class="empty-state" style="grid-column: 1 / -1;">
        <svg class="empty-svg" viewBox="0 0 24 24"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
        <p>Unable to load jobs. Please make sure the backend server is running.</p>
      </div>`;
  }
}

loadProfile();
