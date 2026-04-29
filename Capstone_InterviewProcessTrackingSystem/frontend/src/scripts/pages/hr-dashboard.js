import { getAllCandidates, deleteCandidate as deleteCandidateAction } from "../actions/candidate.js";
import { getAllJobs as fetchAllJobs, createJob as createJobAction, updateJob as updateJobAction, deactivateJob as deactivateJobAction, activateJob as activateJobAction, getActiveJobs } from "../actions/job.js";
import { getAllInterviews as fetchInterviews, scheduleInterview as scheduleInterviewAction, deleteInterview as deleteInterviewAction, assignPanel as assignPanelAction, progressStage as progressStageAction, updateInterviewStatus as updateStatusAction } from "../actions/interview.js";
import { getAllPanels as fetchPanels, createPanel as createPanelAction, updatePanel as updatePanelAction, deletePanel as deletePanelAction } from "../actions/panel.js";
import { submitFeedback as submitFeedbackAction, getFeedbackByInterview } from "../actions/feedback.js";

const token = localStorage.getItem("token");
if (!token || localStorage.getItem("role") !== "HR") { window.location.href = "sign-in/index.html"; }
let cachedJobs = [];
let cachedPanels = [];

function toast(msg, type="success") {
  const c = document.getElementById("toastContainer");
  const t = document.createElement("div"); t.className = "toast " + type; t.textContent = msg;
  c.appendChild(t); setTimeout(() => t.remove(), 3000);
}
function openModal(id) { document.getElementById(id).classList.add("active"); }
function closeModal(id) { document.getElementById(id).classList.remove("active"); }
function logout() { localStorage.clear(); window.location.href = "sign-in/index.html"; }

function showTab(name, el) {
  ["candidates","jobs","interviews","panels"].forEach(t => {
    document.getElementById("tab-"+t).style.display = t===name?"block":"none";
  });
  document.querySelectorAll(".sidebar-nav a").forEach(a => a.classList.remove("active"));
  if (el) el.classList.add("active");
  if (name==="candidates") loadCandidates();
  if (name==="jobs") loadJobs();
  if (name==="interviews") loadInterviews();
  if (name==="panels") loadPanels();
}

// ===== CANDIDATES =====
async function loadCandidates() {
  try {
    const data = await getAllCandidates();
    const list = data.data || [];
    const body = document.getElementById("candBody");
    const total=list.length, selected=list.filter(c=>c.applicationStatus==="SELECTED").length,
          rejected=list.filter(c=>c.applicationStatus==="REJECTED").length, inProgress=total-selected-rejected;
    document.getElementById("candStats").innerHTML = `
      <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg></div><div><div class="stat-value">${total}</div><div class="stat-label">Total</div></div></div>
      <div class="stat-card"><div class="stat-icon" style="background:#fef3c7"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#f59e0b" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg></div><div><div class="stat-value">${inProgress}</div><div class="stat-label">In Progress</div></div></div>
      <div class="stat-card"><div class="stat-icon" style="background:#d1fae5"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg></div><div><div class="stat-value">${selected}</div><div class="stat-label">Selected</div></div></div>
      <div class="stat-card"><div class="stat-icon" style="background:#fee2e2"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#ef4444" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg></div><div><div class="stat-value">${rejected}</div><div class="stat-label">Rejected</div></div></div>`;
    if (!list.length) { body.innerHTML='<tr><td colspan="6" class="empty-state">No candidates yet.</td></tr>'; return; }
    body.innerHTML = list.map(c =>
      '<tr><td><strong>C-'+c.id+'</strong></td><td>'+( c.fullName||'N/A')+'</td><td>'+(c.jobTitle||'N/A')+'</td>' +
      '<td><span class="badge badge-info">'+(c.currentStage||'N/A')+'</span></td>' +
      '<td><span class="badge '+(c.applicationStatus==='SELECTED'?'badge-success':c.applicationStatus==='REJECTED'?'badge-danger':'badge-warning')+'">'+(c.applicationStatus||'N/A')+'</span></td>' +
      '<td><div class="actions-cell"><div class="dropdown">' +
      '<button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>' +
      '<div class="dropdown-content">' +
      '<button onclick="openStageModal('+c.id+')">Progress Stage</button>' +
      (c.resumeUrl ? '<a href="'+c.resumeUrl+'" target="_blank">View Resume</a>' : '') +
      '<button style="color: var(--danger)" onclick="deleteCandidate('+c.id+')">Delete Candidate</button>' +
      '</div></div></div></td></tr>'
    ).join("");
  } catch(e) { console.error("loadCandidates error:", e); }
}

function openStageModal(id) { document.getElementById("spCandId").value=id; openModal("stageModal"); }

async function progressStage(e) {
  e.preventDefault();
  const body = { candidateId: parseInt(document.getElementById("spCandId").value), newStage: document.getElementById("spStage").value };
  try {
    const data = await progressStageAction(body);
    if (data.success) { toast("Stage updated!"); closeModal("stageModal"); loadCandidates(); }
    else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

async function deleteCandidate(id) {
  if (!confirm("Delete candidate C-"+id+"?")) return;
  try {
    const data = await deleteCandidateAction(id);
    if (data.success) { toast("Candidate deleted"); loadCandidates(); }
    else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

// ===== JOBS =====
async function loadJobs() {
  try {
    const data = await fetchAllJobs();
    const list = data.data || [];
    cachedJobs = list;
    const activeCount = list.filter(j => j.isActive !== false).length;
    document.getElementById("jobStats").innerHTML = `
      <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg></div><div><div class="stat-value">${activeCount}</div><div class="stat-label">Active Jobs</div></div></div>
      <div class="stat-card"><div class="stat-icon" style="background:#f3f4f6"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#6b7280" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg></div><div><div class="stat-value">${list.length}</div><div class="stat-label">Total Jobs</div></div></div>`;
    const grid = document.getElementById("jobsGrid");
    if (!list.length) { grid.innerHTML='<div class="empty-state">No jobs yet.</div>'; return; }
    grid.innerHTML = list.map(j => `<div class="job-card ${j.isActive === false ? 'job-inactive' : ''}">
      <div class="job-header">
        <h4 class="job-title">${j.title}</h4>
        <div class="dropdown">
          <button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>
          <div class="dropdown-content">
            <button onclick="openEditJobModal(${j.id})">Edit Details</button>
            ${j.isActive === false ? 
              `<button onclick="activateJob(${j.id})">Activate Job</button>` : 
              `<button style="color: var(--danger)" onclick="deactivateJob(${j.id})">Deactivate Job</button>`
            }
          </div>
        </div>
      </div>
      <div class="job-id-row" style="margin-bottom: 12px;">
        <span class="job-id">J-${j.id}</span>
        <span class="badge ${j.isActive === false ? 'badge-danger' : 'badge-success'}">${j.isActive === false ? 'Inactive' : 'Active'}</span>
      </div>
      <div class="job-detail"><svg viewBox="0 0 24 24" width="16" height="16"><path fill="#64748b" d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/></svg> ${j.location}</div>
      <div class="job-detail"><svg viewBox="0 0 24 24" width="16" height="16"><path fill="#64748b" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg> ${j.minExperience}-${j.maxExperience} yrs</div>
      <div class="job-detail"><svg viewBox="0 0 24 24" width="16" height="16"><path fill="#64748b" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/></svg> ${j.minSalary?j.minSalary+'-'+j.maxSalary+' LPA':'Not specified'}</div>
      <span class="job-type-badge">${(j.jobType||'FULL_TIME').replace(/_/g,' ')}</span>
    </div>`).join("");
  } catch(e) { console.error("loadJobs error:", e); }
}

async function createJob(e) {
  e.preventDefault();
  const skills = document.getElementById("jSkills").value;
  const body = {
    title: document.getElementById("jTitle").value.trim(), description: document.getElementById("jDesc").value.trim(),
    skills: skills ? skills.split(",").map(s=>s.trim()).filter(s=>s) : [],
    location: document.getElementById("jLoc").value.trim(),
    minExperience: parseInt(document.getElementById("jMinExp").value||"0"),
    maxExperience: parseInt(document.getElementById("jMaxExp").value||"0"),
    minSalary: parseFloat(document.getElementById("jMinSal").value||"0"),
    maxSalary: parseFloat(document.getElementById("jMaxSal").value||"0"),
    jobType: document.getElementById("jType").value
  };
  if (!body.title || !body.description || !body.location) {
    document.getElementById("jMsg").className="msg error"; 
    document.getElementById("jMsg").textContent="Please fill out all required fields";
    return;
  }
  
  try {
    const data = await createJobAction(body);
    if (data.success) { toast("Job created!"); closeModal("createJobModal"); document.getElementById("createJobForm").reset(); loadJobs(); }
    else { document.getElementById("jMsg").className="msg error"; document.getElementById("jMsg").textContent=data.message||"Failed"; }
  } catch(e) { toast("Server error","error"); }
}

async function deactivateJob(id) {
  if (!confirm("Deactivate job J-"+id+"?")) return;
  try {
    const data = await deactivateJobAction(id);
    if (data.success) { toast("Job deactivated!"); loadJobs(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

window.openEditJobModal = function(id) {
  const job = cachedJobs.find(j => j.id === id);
  if(!job) return;
  document.getElementById("ejId").value = job.id;
  document.getElementById("ejTitle").value = job.title || '';
  document.getElementById("ejDesc").value = job.description || '';
  document.getElementById("ejSkills").value = (job.skills || []).join(', ');
  document.getElementById("ejLoc").value = job.location || '';
  document.getElementById("ejMinExp").value = job.minExperience || 0;
  document.getElementById("ejMaxExp").value = job.maxExperience || 0;
  document.getElementById("ejMinSal").value = job.minSalary || '';
  document.getElementById("ejMaxSal").value = job.maxSalary || '';
  document.getElementById("ejType").value = job.jobType || 'FULL_TIME';
  openModal("editJobModal");
};

window.updateJob = async function(e) {
  e.preventDefault();
  const id = document.getElementById("ejId").value;
  const body = {
    title: document.getElementById("ejTitle").value.trim(),
    description: document.getElementById("ejDesc").value.trim(),
    skills: document.getElementById("ejSkills").value.split(',').map(s=>s.trim()).filter(s=>s),
    location: document.getElementById("ejLoc").value.trim(),
    minExperience: parseInt(document.getElementById("ejMinExp").value) || 0,
    maxExperience: parseInt(document.getElementById("ejMaxExp").value) || 0,
    minSalary: parseFloat(document.getElementById("ejMinSal").value) || null,
    maxSalary: parseFloat(document.getElementById("ejMaxSal").value) || null,
    jobType: document.getElementById("ejType").value
  };
  if (!body.title || !body.description || !body.location) {
    document.getElementById("ejMsg").className="msg error"; 
    document.getElementById("ejMsg").textContent="Please fill out all required fields";
    return;
  }

  try {
    const data = await updateJobAction(id, body);
    if(data.success) {
      toast("Job updated successfully");
      closeModal("editJobModal");
      loadJobs();
    } else {
      document.getElementById("ejMsg").textContent = data.message;
      document.getElementById("ejMsg").className = "msg error";
    }
  } catch(err) {
    document.getElementById("ejMsg").textContent = "Error updating job";
    document.getElementById("ejMsg").className = "msg error";
  }
};

window.activateJob = async function(id) {
  if (!confirm("Activate job J-"+id+"?")) return;
  try {
    const data = await activateJobAction(id);
    if (data.success) { toast("Job activated!"); loadJobs(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

// ===== INTERVIEWS =====
async function loadInterviews() {
  try {
    const data = await fetchInterviews();
    const list = data.data || [];
    document.getElementById("intStats").innerHTML = `
      <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/></svg></div><div><div class="stat-value">${list.length}</div><div class="stat-label">Total</div></div></div>`;
    const body = document.getElementById("intBody");
    if (!list.length) { body.innerHTML='<tr><td colspan="6" class="empty-state">No interviews yet.</td></tr>'; return; }
    body.innerHTML = list.map(i => `<tr>
      <td><strong>I-${i.id}</strong></td><td>${i.candidateName||'N/A'}</td>
      <td><span class="badge badge-info">${i.stage||'N/A'}</span></td>
      <td>${i.interviewDateTime?new Date(i.interviewDateTime).toLocaleString():'TBD'}</td>
      <td><span class="badge ${i.status==='COMPLETED'?'badge-success':i.status==='CANCELLED'?'badge-danger':i.status==='NO_SHOW'?'badge-danger':'badge-warning'}">${i.status||'N/A'}</span></td>
      <td>
        <div class="actions-cell">
          <div class="dropdown">
            <button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>
            <div class="dropdown-content">
              <button onclick="openAssignPanel(${i.id})">Assign Panel</button>
              <button onclick="openGiveFeedback(${i.id})">Submit Feedback</button>
              <button onclick="viewFeedback(${i.id})">View Feedbacks</button>
              <button onclick="openUpdateStatus(${i.id}, '${i.status}')">Update Status</button>
              <button style="color: var(--danger)" onclick="deleteInterview(${i.id})">Delete</button>
            </div>
          </div>
        </div>
      </td></tr>`).join("");
  } catch(e) { console.error("loadInterviews error:", e); }
}

window.openGiveFeedback = function(id) {
  document.getElementById("gfIntId").value = id;
  openModal("giveFeedbackModal");
};

window.openUpdateStatus = function(id, currentStatus) {
  document.getElementById("usIntId").value = id;
  document.getElementById("usStatus").value = currentStatus || "SCHEDULED";
  openModal("updateStatusModal");
};

window.handleUpdateStatus = async function(e) {
  e.preventDefault();
  const id = document.getElementById("usIntId").value;
  const status = document.getElementById("usStatus").value;
  const msgEl = document.getElementById("usMsg");
  msgEl.style.display = "none";

  try {
    const data = await updateStatusAction(id, status);
    if (data.success) {
      toast("Status updated!");
      closeModal("updateStatusModal");
      loadInterviews();
    } else {
      msgEl.className = "msg error";
      msgEl.textContent = data.message || "Update failed";
      msgEl.style.display = "block";
    }
  } catch(err) {
    toast("Server error", "error");
  }
};

window.submitFeedback = async function(e) {
  e.preventDefault();
  const msgEl = document.getElementById("gfMsg");
  msgEl.style.display = "none";
  const body = {
    interviewId: parseInt(document.getElementById("gfIntId").value),
    panelId: null, // HR is submitting
    rating: parseInt(document.getElementById("gfRating").value),
    status: document.getElementById("gfStatus").value,
    comments: document.getElementById("gfComments").value,
    strengths: document.getElementById("gfStrengths").value,
    weaknesses: document.getElementById("gfWeaknesses").value,
    areasCovered: document.getElementById("gfAreas").value
  };
  try {
    const data = await submitFeedbackAction(body);
    if (data.success) {
      toast("Feedback submitted!");
      document.getElementById("giveFeedbackForm").reset();
      closeModal("giveFeedbackModal");
    } else {
      msgEl.className = "msg error";
      msgEl.textContent = data.message || "Failed";
      msgEl.style.display = "block";
    }
  } catch(err) {
    msgEl.className = "msg error";
    msgEl.textContent = "Server error";
    msgEl.style.display = "block";
  }
};

async function openScheduleModal() {
  try {
    const [cData, jData] = await Promise.all([getAllCandidates(), getActiveJobs()]);
    const cands = cData.data||[]; const jobs = jData.data||[];
    document.getElementById("siCand").innerHTML = cands.map(c=>'<option value="'+c.id+'">C-'+c.id+' '+(c.fullName||'')+'</option>').join("");
    document.getElementById("siJob").innerHTML = jobs.map(j=>'<option value="'+j.id+'">J-'+j.id+' '+j.title+'</option>').join("");
  } catch(e) { console.error(e); }
  openModal("scheduleModal");
}

async function scheduleInterview(e) {
  e.preventDefault();
  const interviewDate = document.getElementById("siDate").value;
  
  // Validate: interview date must be in the future
  if (new Date(interviewDate) <= new Date()) {
    document.getElementById("siMsg").className="msg error";
    document.getElementById("siMsg").textContent="Interview date must be a future date";
    return;
  }
  
  const body = {
    candidateId: parseInt(document.getElementById("siCand").value),
    jobDescriptionId: parseInt(document.getElementById("siJob").value),
    stage: document.getElementById("siStage").value,
    interviewDateTime: interviewDate,
    focusArea: document.getElementById("siFocus").value.trim()
  };
  
  if (!body.focusArea) {
    document.getElementById("siMsg").className="msg error";
    document.getElementById("siMsg").textContent="Focus area is required";
    return;
  }
  
  try {
    const data = await scheduleInterviewAction(body);
    if (data.success) { toast("Interview scheduled!"); closeModal("scheduleModal"); loadInterviews(); }
    else { document.getElementById("siMsg").className="msg error"; document.getElementById("siMsg").textContent=data.message||"Failed"; }
  } catch(e) { toast("Server error","error"); }
}

async function openAssignPanel(interviewId) {
  document.getElementById("apInterviewId").value = interviewId;
  document.getElementById("assignPanelForm").reset();
  document.getElementById("apMsg").textContent = "";
  try {
    const json = await fetchPanels();
    const items = json.data || [];
    const options = items.map(p=>'<option value="'+p.id+'">P-'+p.id+' '+p.name+'</option>').join("");
    document.getElementById("apPanelId").innerHTML = items.length ? options : '<option value="">No panels</option>';
    document.getElementById("apPanelId2").innerHTML = '<option value="">-- Select Second Panel --</option>' + options;
  } catch(e) { console.error(e); }
  openModal("assignPanelModal");
}

async function assignPanel(e) {
  e.preventDefault();
  const interviewId = parseInt(document.getElementById("apInterviewId").value);
  const panel1Id = document.getElementById("apPanelId").value;
  const panel1Focus = document.getElementById("apFocus").value;
  const panel2Id = document.getElementById("apPanelId2").value;
  const panel2Focus = document.getElementById("apFocus2").value;

  if (panel1Id === panel2Id && panel1Id) {
    document.getElementById("apMsg").className="msg error"; 
    document.getElementById("apMsg").textContent="Cannot assign the same panel member twice.";
    return;
  }

  try {
    // Assign Panel 1
    const p1Data = await assignPanelAction({ interviewId, panelId: parseInt(panel1Id), focusArea: panel1Focus });
    
    if (!p1Data.success) {
      document.getElementById("apMsg").className="msg error"; 
      document.getElementById("apMsg").textContent=p1Data.message||"Failed to assign Panel 1";
      return;
    }
    
    // Assign Panel 2 if selected
    if (panel2Id) {
      if (!panel2Focus) {
         document.getElementById("apMsg").className="msg error"; 
         document.getElementById("apMsg").textContent="Focus Area is required for Panel 2";
         return;
      }
      const p2Data = await assignPanelAction({ interviewId, panelId: parseInt(panel2Id), focusArea: panel2Focus });
      
      if (!p2Data.success) {
        document.getElementById("apMsg").className="msg error"; 
        document.getElementById("apMsg").textContent=p2Data.message||"Failed to assign Panel 2. Panel 1 was assigned.";
        return;
      }
    }
    
    toast("Panel(s) assigned successfully!"); 
    closeModal("assignPanelModal"); 
    loadInterviews();
  } catch(e) { toast("Server error","error"); }
}

async function viewFeedback(interviewId) {
  openModal("feedbackModal");
  try {
    const data = await getFeedbackByInterview(interviewId);
    const list = data.data || [];
    if (!list.length) { document.getElementById("feedbackContent").innerHTML='<p class="empty-state">No feedback yet</p>'; return; }
    document.getElementById("feedbackContent").innerHTML = list.map(f =>
      '<div class="feedback-card"><p><strong>Rating:</strong> <span class="rating">'+'★'.repeat(f.rating||0)+'☆'.repeat(5-(f.rating||0))+'</span></p>' +
      '<p><strong>Decision:</strong> <span class="'+(f.status==="SELECTED"?"decision-selected":"decision-rejected")+'">'+(f.status==="SELECTED"?"Selected":"Rejected")+'</span></p>' +
      '<p><strong>Comments:</strong> '+(f.comments||'--')+'</p>' +
      '<p><strong>Strengths:</strong> '+(f.strengths||'--')+'</p>' +
      '<p><strong>Weaknesses:</strong> '+(f.weaknesses||'--')+'</p></div>'
    ).join("");
  } catch(e) { document.getElementById("feedbackContent").innerHTML='<p class="empty-state">Error loading</p>'; }
}

async function deleteInterview(id) {
  if (!confirm("Delete interview I-"+id+"?")) return;
  try {
    const data = await deleteInterviewAction(id);
    if (data.success) { toast("Interview deleted"); loadInterviews(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

// ===== PANELS =====
async function loadPanels() {
  try {
    const json = await fetchPanels();
    const list = json.data || [];
    cachedPanels = list;
    const body = document.getElementById("panelsBody");
    if (!list.length) { body.innerHTML='<tr><td colspan="7" class="empty-state">No panels yet.</td></tr>'; return; }
    body.innerHTML = list.map(p => {
      return '<tr><td><strong>P-'+p.id+'</strong></td><td><strong>'+p.name+'</strong></td><td>'+p.email+'</td>' +
      '<td>'+(p.organization||'--')+'</td><td>'+(p.designation||'--')+'</td><td>'+(p.mobileNumber||'--')+'</td>' +
      '<td><div class="actions-cell"><div class="dropdown">' +
      '<button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>' +
      '<div class="dropdown-content">' +
      '<button onclick="openEditPanel('+p.id+')">Edit Member</button>' +
      '<button style="color: var(--danger)" onclick="deletePanelMember('+p.id+')">Remove Member</button>' +
      '</div></div></div></td></tr>';
    }).join("");
  } catch(e) { console.error("loadPanels error:", e); }
}

function openEditPanel(id) {
  const panel = cachedPanels.find(p => p.id === id);
  if(!panel) return;
  document.getElementById("epId").value = panel.id;
  document.getElementById("epOrg").value = panel.organization || '';
  document.getElementById("epDesig").value = panel.designation || '';
  document.getElementById("epMobile").value = panel.mobileNumber || '';
  document.getElementById("epEmail").value = panel.email || '';
  openModal("editPanelModal");
}

async function updatePanel(e) {
  e.preventDefault();
  const id = document.getElementById("epId").value;
  const body = { email:document.getElementById("epEmail").value.trim(), organization:document.getElementById("epOrg").value.trim(), designation:document.getElementById("epDesig").value.trim(), mobileNumber:document.getElementById("epMobile").value.trim() };
  
  if (!body.email || !body.mobileNumber) {
    document.getElementById("epMsg").className="msg error"; 
    document.getElementById("epMsg").textContent="Email and Mobile are required";
    return;
  }
  
  try {
    const data = await updatePanelAction(id, body);
    if (data.success) { toast("Panel updated!"); closeModal("editPanelModal"); loadPanels(); }
    else { document.getElementById("epMsg").className="msg error"; document.getElementById("epMsg").textContent=data.message||"Failed"; }
  } catch(e) { toast("Server error","error"); }
}

async function createPanel(e) {
  e.preventDefault();
  const body = {
    fullName: document.getElementById("cpName").value.trim(), email: document.getElementById("cpEmail").value.trim(),
    organization: document.getElementById("cpOrg").value.trim(), designation: document.getElementById("cpDesig").value.trim(),
    mobileNumber: document.getElementById("cpMobile").value.trim()
  };
  
  if (!body.fullName || !body.email || !body.mobileNumber) {
    document.getElementById("cpMsg").className="msg error"; 
    document.getElementById("cpMsg").textContent="Full Name, Email and Mobile are required";
    return;
  }
  
  try {
    const data = await createPanelAction(body);
    if (data.success) { toast("Panel created!"); closeModal("panelModal"); document.getElementById("panelForm").reset(); loadPanels(); }
    else { document.getElementById("cpMsg").className="msg error"; document.getElementById("cpMsg").textContent=data.message||"Failed"; }
  } catch(e) { toast("Server error","error"); }
}

async function deletePanelMember(id) {
  if (!confirm("Delete panel P-"+id+"?")) return;
  try {
    const data = await deletePanelAction(id);
    if (data.success) { toast("Panel deleted"); loadPanels(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

// Init
loadCandidates();

// Expose functions to window for HTML onclick handlers
window.toast = toast;
window.openModal = openModal;
window.closeModal = closeModal;
window.logout = logout;
window.showTab = showTab;
window.openStageModal = openStageModal;
window.progressStage = progressStage;
window.deleteCandidate = deleteCandidate;
window.createJob = createJob;
window.deactivateJob = deactivateJob;
window.openScheduleModal = openScheduleModal;
window.scheduleInterview = scheduleInterview;
window.openAssignPanel = openAssignPanel;
window.assignPanel = assignPanel;
window.viewFeedback = viewFeedback;
window.deleteInterview = deleteInterview;
window.openEditPanel = openEditPanel;
window.updatePanel = updatePanel;
window.createPanel = createPanel;
window.deletePanelMember = deletePanelMember;
