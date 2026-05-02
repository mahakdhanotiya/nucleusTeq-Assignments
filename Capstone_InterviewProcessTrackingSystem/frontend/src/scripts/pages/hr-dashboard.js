import { getAllCandidates, deleteCandidate as deleteCandidateAction, searchCandidates } from "../actions/candidate.js";
import { getAllJobs as fetchAllJobs, createJob as createJobAction, updateJob as updateJobAction, deactivateJob as deactivateJobAction, activateJob as activateJobAction, getActiveJobs } from "../actions/job.js";
import { getAllInterviews as fetchInterviews, scheduleInterview as scheduleInterviewAction, deleteInterview as deleteInterviewAction, assignPanel as assignPanelAction, progressStage as progressStageAction, updateInterviewStatus as updateStatusAction, updateInterview as updateInterviewAction } from "../actions/interview.js";
import { getAllPanels as fetchPanels, createPanel as createPanelAction, updatePanel as updatePanelAction, deletePanel as deletePanelAction } from "../actions/panel.js";
import { submitFeedback as submitFeedbackAction, getFeedbackByInterview, getFeedbackByCandidate } from "../actions/feedback.js";
import { fetchHandler } from "../lib/handlers/fetch.js";
import { renderSidebarProfile, showFieldError, clearErrors, getTrimmedValues, initFormCleanup } from "../lib/utils/ui.js";
import { APPLICATION_STATUS, APP_ROLES, INTERVIEW_STATUS, INTERVIEW_STAGES } from "../constants/index.js";
import * as Renderers from "./hr-dashboard-renderers.js";

// --- Global State ---
const token = localStorage.getItem("token");
if (!token || localStorage.getItem("role") !== APP_ROLES.HR) { 
  window.location.href = "sign-in/index.html"; 
}

let cachedJobs = [];
let cachedPanels = [];
let cachedInterviews = [];
let cachedCandidates = [];
let cachedFeedback = [];

/**
 * Displays a temporary toast message to the user.
 * @param {string} msg - The message to display.
 * @param {string} type - Toast type ('success' or 'error').
 */
function toast(msg, type="success") {
  const container = document.getElementById("toastContainer");
  const t = document.createElement("div"); 
  t.className = "toast " + type; 
  t.textContent = msg;
  container.appendChild(t); 
  setTimeout(() => t.remove(), 3000);
}
/**
 * Opens a modal by adding the 'active' class.
 * @param {string} id - Modal element ID.
 */
function openModal(id) { document.getElementById(id).classList.add("active"); }

/**
 * Closes a modal by removing the 'active' class.
 * @param {string} id - Modal element ID.
 */
function closeModal(id) { 
  const modal = document.getElementById(id);
  if (!modal) return;
  
  modal.classList.remove("active"); 
  
  // Global Cleanup: Reset any forms inside the modal when it closes
  const forms = modal.querySelectorAll("form");
  forms.forEach(form => {
    form.reset();
    if (form.id) clearErrors(form.id);
  });

  // Clear any status messages (.msg elements)
  const msgs = modal.querySelectorAll(".msg");
  msgs.forEach(m => {
    m.textContent = "";
    m.className = "msg";
    m.style.display = "none";
  });
}

/**
 * Logs out the user and clears local storage.
 */
function logout() { localStorage.clear(); window.location.href = "sign-in/index.html"; }

// Global listener for click-based dropdowns
document.addEventListener("click", function(e) {
  const isDropdownBtn = e.target.closest(".btn-icon");
  const currentDropdown = isDropdownBtn ? isDropdownBtn.closest(".dropdown") : null;

  // If clicking a dropdown button, toggle its menu
  if (currentDropdown) {
    currentDropdown.classList.toggle("active");
  }

  // Close all other dropdowns
  document.querySelectorAll(".dropdown.active").forEach(d => {
    if (d !== currentDropdown) {
      d.classList.remove("active");
    }
  });
});

/**
 * Switches between different dashboard tabs.
 * @param {string} name - Tab name ('candidates', 'jobs', etc.)
 * @param {HTMLElement} el - The clicked link element.
 */
function showTab(name, el) {
  ["candidates","jobs","interviews","panels","feedback-center"].forEach(t => {
    const tab = document.getElementById("tab-"+t);
    if (tab) tab.style.display = t===name?"block":"none";
  });
  document.querySelectorAll(".sidebar-nav a").forEach(a => a.classList.remove("active"));
  if (el) el.classList.add("active");
  
  if (name==="candidates") {
      loadCandidates();
      populateJDFilter();
  }
  if (name==="jobs") loadJobs();
  if (name==="interviews") loadInterviews();
  if (name==="panels") loadPanels();
  if (name==="feedback-center") loadFeedbackCenter();
}

/**
 * Fetches and displays all feedback submissions in the system.
 */
async function loadFeedbackCenter() {
  const grid = document.getElementById("fbCenterGrid");
  grid.innerHTML = '<div class="empty-state">Fetching evaluations...</div>';
  try {
    const data = await fetchHandler("/api/feedback/all", { requireAuth: true });
    const list = data.data || [];
    cachedFeedback = list;
    if (!list.length) {
      grid.innerHTML = '<div class="empty-state">No feedback submitted yet. Evaluations will appear here as interviews conclude.</div>';
      return;
    }
    Renderers.renderFeedbackCenter(list);
  } catch(e) { console.error("loadFeedbackCenter error:", e); }
}

async function refreshAllData() {
  if (document.getElementById("tab-candidates").style.display === "block") loadCandidates();
  if (document.getElementById("tab-interviews").style.display === "block") loadInterviews();
}

/**
 * Fetches and renders the candidate list.
 * @param {Object} [customData] - Optional pre-fetched candidate data.
 */
async function loadCandidates(customData = null) {
  try {
    const list = customData || (await getAllCandidates()).data || [];
    cachedCandidates = list;
    Renderers.renderCandidates(list);
    
    // Also refresh activity feed
    loadActivityFeed();
  } catch(e) { console.error("loadCandidates error:", e); }
}

/**
 * Generates and renders a timeline of recent system events.
 */
async function loadActivityFeed() {
    const feedContainer = document.getElementById("activityFeed");
    if (!feedContainer) return;

    try {
        // We gather data from cached versions if available, or fetch fresh
        // Using separate awaits to prevent one failure from killing the whole feed
        let cands = [], ints = [], fbs = [];
        try { 
            const res = cachedCandidates.length ? {data: cachedCandidates} : await getAllCandidates(); 
            cands = res.data || [];
        } catch(e) { console.warn("Feed: cands fetch failed", e); }
        
        try { 
            const res = cachedInterviews.length ? {data: cachedInterviews} : await fetchInterviews(); 
            ints = res.data || [];
        } catch(e) { console.warn("Feed: ints fetch failed", e); }

        try { 
            const res = cachedFeedback.length ? {data: cachedFeedback} : await fetchHandler("/api/feedback/all", { requireAuth: true }); 
            fbs = res.data || [];
        } catch(e) { console.warn("Feed: fbs fetch failed", e); }

        let activities = [];

        // 1. New Candidates
        cands.slice(-5).forEach(c => {
            activities.push({
                type: 'CANDIDATE',
                title: 'New Application',
                desc: `<strong>${c.fullName}</strong> applied for <strong>${c.jobTitle}</strong>`,
                time: c.id, // Using ID as a proxy for time since we don't have createdDate
                icon: '👤',
                color: '#3b82f6'
            });
        });

        // 2. Scheduled Interviews
        ints.slice(-5).forEach(i => {
            activities.push({
                type: 'INTERVIEW',
                title: 'Interview Scheduled',
                desc: `Round <strong>${i.stage}</strong> set for <strong>${i.candidateName}</strong>`,
                time: i.id,
                icon: '📅',
                color: '#f59e0b'
            });
        });

        // 3. New Feedback
        fbs.slice(-5).forEach(f => {
            activities.push({
                type: 'FEEDBACK',
                title: 'Feedback Received',
                desc: `<strong>${f.panelName || 'HR'}</strong> evaluated <strong>${f.candidateName}</strong>`,
                time: f.id,
                icon: '📝',
                color: '#10b981'
            });
        });

        // Sort by ID descending (proxy for latest)
        activities.sort((a, b) => b.time - a.time);
        Renderers.renderActivityFeed(activities);
    } catch (e) {
        console.error("Feed error:", e);
        document.getElementById("activityFeed").innerHTML = '<div class="empty-state">Unable to load activity.</div>';
    }
}

/**
 * Populates the job description filter dropdown.
 */
async function populateJDFilter() {
    try {
        const data = await fetchAllJobs();
        const jobs = data.data || [];
        const select = document.getElementById("filterJD");
        select.innerHTML = '<option value="">All Jobs</option>' + 
            jobs.map(j => `<option value="${j.id}">${j.title} (J-${j.id})</option>`).join("");
    } catch(e) { console.error("Error populating JD filter", e); }
}

/**
 * Applies search and filter criteria to the candidate list.
 */
async function applyFilters() {
    const jdId = document.getElementById("filterJD").value;
    const stage = document.getElementById("filterStage").value;
    const status = document.getElementById("filterStatus").value;
    const name = document.getElementById("searchCandidates").value;
    
    try {
        toast("Searching...");
        const data = await searchCandidates(jdId, stage, status, name);
        if (data.success) {
            loadCandidates(data.data);
        } else {
            toast(data.message || "Search failed", "error");
        }
    } catch(e) { toast("Server error during search", "error"); }
}

async function resetFilters() {
    document.getElementById("filterJD").value = "";
    document.getElementById("filterStage").value = "";
    document.getElementById("filterStatus").value = "";
    document.getElementById("searchCandidates").value = "";
    loadCandidates();
}

async function openStageModal(id) { document.getElementById("spCandId").value = id; openModal("stageModal"); }
async function progressStage(e) {
  e.preventDefault();
  try {
    const data = await progressStageAction({ candidateId: parseInt(document.getElementById("spCandId").value), newStage: document.getElementById("spStage").value });
    if (data.success) { 
      toast("Stage updated!"); 
      closeModal("stageModal"); 
      refreshAllData();
    }
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

/**
 * Fetches and renders all job descriptions.
 */
async function loadJobs() {
  try {
    const data = await fetchAllJobs();
    const list = data.data || [];
    cachedJobs = list;
    Renderers.renderJobs(list);
  } catch(e) { console.error("loadJobs error:", e); }
}

async function createJob(e) {
  e.preventDefault();
  clearErrors("createJobForm");
  const vals = getTrimmedValues("createJobForm");
  
  let hasError = false;
  if (!vals.jTitle) { showFieldError("jTitle", "Job Title is required"); hasError = true; }
  if (!vals.jDesc) { showFieldError("jDesc", "Description is required"); hasError = true; }
  if (!vals.jLoc) { showFieldError("jLoc", "Location is required"); hasError = true; }
  
  const minExp = parseInt(vals.jMinExp || "0");
  const maxExp = parseInt(vals.jMaxExp || "0");
  if (minExp > maxExp) { showFieldError("jMinExp", "Min Exp cannot be > Max Exp"); hasError = true; }

  const minSal = parseFloat(vals.jMinSal || "0");
  const maxSal = parseFloat(vals.jMaxSal || "0");
  if (minSal > maxSal && maxSal > 0) { showFieldError("jMinSal", "Min Salary cannot be > Max Salary"); hasError = true; }

  if (hasError) return;

  const body = {
    title: vals.jTitle, 
    description: vals.jDesc,
    skills: vals.jSkills ? vals.jSkills.split(",").map(s=>s.trim()).filter(s=>s) : [],
    location: vals.jLoc,
    minExperience: minExp,
    maxExperience: maxExp,
    minSalary: minSal,
    maxSalary: maxSal,
    jobType: vals.jType
  };
  
  try {
    const data = await createJobAction(body);
    if (data.success) { 
      toast("Job created!"); 
      closeModal("createJobModal"); 
      document.getElementById("createJobForm").reset(); 
      loadJobs(); 
    } else { 
      document.getElementById("jMsg").className="msg error"; 
      document.getElementById("jMsg").textContent=data.message||"Failed"; 
    }
  } catch(e) { toast("Server error","error"); }
}

function openEditJobModal(id) {
  const j = cachedJobs.find(x=>x.id===id);
  if (!j) return;
  document.getElementById("ejId").value = j.id;
  document.getElementById("ejTitle").value = j.title;
  document.getElementById("ejDesc").value = j.description;
  document.getElementById("ejSkills").value = (j.skills||[]).join(", ");
  document.getElementById("ejLoc").value = j.location;
  document.getElementById("ejMinExp").value = j.minExperience;
  document.getElementById("ejMaxExp").value = j.maxExperience;
  document.getElementById("ejMinSal").value = j.minSalary;
  document.getElementById("ejMaxSal").value = j.maxSalary;
  document.getElementById("ejType").value = j.jobType || "FULL_TIME";
  openModal("editJobModal");
}

async function updateJob(e) {
  e.preventDefault();
  clearErrors("editJobForm");
  const vals = getTrimmedValues("editJobForm");
  
  let hasError = false;
  if (!vals.ejTitle) { showFieldError("ejTitle", "Job Title is required"); hasError = true; }
  if (!vals.ejDesc) { showFieldError("ejDesc", "Description is required"); hasError = true; }
  if (!vals.ejLoc) { showFieldError("ejLoc", "Location is required"); hasError = true; }
  
  const minExp = parseInt(vals.ejMinExp || "0");
  const maxExp = parseInt(vals.ejMaxExp || "0");
  if (minExp > maxExp) { showFieldError("ejMinExp", "Min Exp cannot be > Max Exp"); hasError = true; }

  const minSal = parseFloat(vals.ejMinSal || "0");
  const maxSal = parseFloat(vals.ejMaxSal || "0");
  if (minSal > maxSal && maxSal > 0) { showFieldError("ejMinSal", "Min Salary cannot be > Max Salary"); hasError = true; }

  if (hasError) return;

  const id = parseInt(vals.ejId);
  const body = {
    title: vals.ejTitle, 
    description: vals.ejDesc,
    skills: vals.ejSkills ? vals.ejSkills.split(",").map(s=>s.trim()).filter(s=>s) : [],
    location: vals.ejLoc,
    minExperience: minExp,
    maxExperience: maxExp,
    minSalary: minSal,
    maxSalary: maxSal,
    jobType: vals.ejType
  };
  
  try {
    const data = await updateJobAction(id, body);
    if (data.success) { 
      toast("Job updated!"); 
      closeModal("editJobModal"); 
      loadJobs(); 
    } else { 
      document.getElementById("ejMsg").className="msg error"; 
      document.getElementById("ejMsg").textContent=data.message||"Failed"; 
    }
  } catch(e) { toast("Server error","error"); }
}

async function deactivateJob(id) {
  if (!confirm("Deactivate job J-"+id+"?")) return;
  try {
    const data = await deactivateJobAction(id);
    if (data.success) { toast("Job deactivated!"); loadJobs(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

async function activateJob(id) {
  if (!confirm("Activate job J-"+id+"?")) return;
  try {
    const data = await activateJobAction(id);
    if (data.success) { toast("Job activated!"); loadJobs(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

/**
 * Fetches and renders all interviews.
 */
async function loadInterviews() {
  try {
    const data = await fetchInterviews();
    const list = data.data || [];
    cachedInterviews = list;
    Renderers.renderInterviews(list);
  } catch(e) { console.error("loadInterviews error:", e); }
}

async function loadPanels() {
  try {
    const data = await fetchPanels();
    const list = data.data || [];
    cachedPanels = list;
    Renderers.renderPanels(list);
  } catch(e) { console.error("loadPanels error:", e); }
}

window.openEditInterview = function(id) {
  const int = cachedInterviews.find(i => i.id === id);
  if (!int) return;
  document.getElementById("eiId").value = id;
  // Convert localdatetime to datetime-local format
  if (int.interviewDateTime) {
    const dt = new Date(int.interviewDateTime);
    dt.setMinutes(dt.getMinutes() - dt.getTimezoneOffset());
    document.getElementById("eiDateTime").value = dt.toISOString().slice(0, 16);
  } else {
    document.getElementById("eiDateTime").value = "";
  }
  document.getElementById("eiFocus").value = int.focusArea || "";
  openModal("editInterviewModal");
};

window.handleEditInterview = async function(e) {
  e.preventDefault();
  const id = document.getElementById("eiId").value;
  const body = {
    interviewDateTime: document.getElementById("eiDateTime").value,
    focusArea: document.getElementById("eiFocus").value
  };
  try {
    const data = await updateInterviewAction(id, body);
    if (data.success) {
      toast("Interview updated!");
      closeModal("editInterviewModal");
      loadInterviews();
    } else {
      toast(data.message || "Failed to update", "error");
    }
  } catch(e) {
    toast("Server error", "error");
  }
};


window.openGiveFeedback = function(id) {
  document.getElementById("gfIntId").value = id;
  openModal("giveFeedbackModal");
};

window.openUpdateStatus = function(id, currentStatus) {
  document.getElementById("usIntId").value = id;
  document.getElementById("usStatus").value = currentStatus;
  openModal("updateStatusModal");
};

async function handleUpdateStatus(e) {
  e.preventDefault();
  const id = document.getElementById("usIntId").value;
  const status = document.getElementById("usStatus").value;
  try {
    const data = await updateStatusAction(id, status);
    if (data.success) { toast("Status updated!"); closeModal("updateStatusModal"); loadInterviews(); }
    else toast(data.message||"Failed", "error");
  } catch(e) { toast("Server error","error"); }
}

async function openScheduleModal() {
  try {
    // Refresh interviews and candidates to ensure latest status
    const [intData, candData, jobData] = await Promise.all([
      fetchInterviews(),
      getAllCandidates(),
      getActiveJobs()
    ]);
    
    cachedInterviews = intData.data || [];
    const activeCandidates = (candData.data || []).filter(c => 
      c.applicationStatus !== APPLICATION_STATUS.REJECTED && 
      c.applicationStatus !== APPLICATION_STATUS.SELECTED
    );

    const candSelect = document.getElementById("siCand");
    const jobSelect = document.getElementById("siJob");
    const stageSelect = document.getElementById("siStage");
    const siMsg = document.getElementById("siMsg");
    const submitBtn = document.getElementById("siSubmitBtn");

    // Populate Jobs first so auto-select works
    jobSelect.innerHTML = '<option value="">-- Select Job --</option>' + 
      (jobData.data || []).map(j => `<option value="${j.id}">${j.title} (J-${j.id})</option>`).join("");
    
    candSelect.innerHTML = '<option value="">-- Select Candidate --</option>' + 
      activeCandidates.map(c => `<option value="${c.id}" data-stage="${c.currentStage}" data-jobid="${c.jobId || ''}" data-jobtitle="${c.jobTitle || ''}">${c.fullName} (C-${c.id})</option>`).join("");
    
    // Reset modal state
    siMsg.textContent = "";
    siMsg.style.display = "none";
    submitBtn.disabled = false;
    jobSelect.disabled = false;

    // 2. Validation Logic
    const validateSchedule = () => {
      const candId = parseInt(candSelect.value);
      const stage = stageSelect.value;
      
      siMsg.textContent = "";
      siMsg.style.display = "none";
      submitBtn.disabled = false;
      clearErrors("scheduleForm");

      if (!candId) return;

      const candInterviews = cachedInterviews.filter(i => i.candidateId == candId);
      
      // 1. Check for Active/Pending Rounds
      const pendingInterviews = candInterviews.filter(i => i.status !== "COMPLETED" && i.status !== "CANCELLED" && i.status !== "REJECTED");
      if (pendingInterviews.length > 0) {
        siMsg.textContent = `Cannot schedule: Round '${pendingInterviews[0].stage}' is still ${pendingInterviews[0].status}. Please complete it first.`;
        siMsg.className = "msg error";
        siMsg.style.display = "block";
        submitBtn.disabled = true;
        return;
      }

      // 2. Check for Stage Sequence
      const completedStages = candInterviews.filter(i => i.status === "COMPLETED").map(i => i.stage);
      
      if (stage === "L2" && !completedStages.includes("L1")) {
        siMsg.textContent = "Cannot schedule L2: L1 must be COMPLETED first.";
        siMsg.className = "msg error";
        siMsg.style.display = "block";
        submitBtn.disabled = true;
        return;
      }
      
      if (stage === "HR" && !completedStages.includes("L2")) {
        siMsg.textContent = "Cannot schedule HR: L2 must be COMPLETED first.";
        siMsg.className = "msg error";
        siMsg.style.display = "block";
        submitBtn.disabled = true;
        return;
      }

      // 3. Prevent duplicate COMPLETED stages
      if (completedStages.includes(stage)) {
        siMsg.textContent = `This candidate has already COMPLETED the ${stage} round.`;
        siMsg.className = "msg error";
        siMsg.style.display = "block";
        submitBtn.disabled = true;
        return;
      }
    };

    // Auto-update stage and job when candidate selected
    candSelect.onchange = (e) => {
      const opt = e.target.options[e.target.selectedIndex];
      
      if (opt && opt.value) {
        const jobId = opt.dataset.jobid;
        const jobTitle = opt.dataset.jobtitle;
        
        // Auto-fill Stage and Job
        if (opt.dataset.stage) stageSelect.value = opt.dataset.stage;
        
        if (jobId) {
            let exists = false;
            for (let i = 0; i < jobSelect.options.length; i++) {
                if (jobSelect.options[i].value === jobId) { exists = true; break; }
            }
            if (!exists && jobTitle) {
                const newOpt = new Option(`${jobTitle} (J-${jobId})`, jobId);
                jobSelect.add(newOpt);
            }
            jobSelect.value = jobId;
            jobSelect.disabled = true;
        } else {
            jobSelect.disabled = false;
            jobSelect.value = "";
        }
      } else {
        jobSelect.value = "";
        jobSelect.disabled = false;
      }
      validateSchedule();
    };

    stageSelect.onchange = validateSchedule;

    openModal("scheduleModal");
  } catch(e) { 
    console.error("Error opening schedule modal:", e);
    toast("Error loading data", "error"); 
  }
}

async function scheduleInterview(e) {
  e.preventDefault();
  clearErrors("scheduleForm");
  const vals = getTrimmedValues("scheduleForm");
  
  let hasError = false;
  if (!vals.siCand) { showFieldError("siCand", "Please select a candidate"); hasError = true; }
  if (!vals.siJob) { showFieldError("siJob", "Please select a job"); hasError = true; }
  if (!vals.siStage) { showFieldError("siStage", "Please select a stage"); hasError = true; }
  if (!vals.siDate) { showFieldError("siDate", "Date & Time is required"); hasError = true; }
  if (!vals.siFocus) { showFieldError("siFocus", "Focus Area is required"); hasError = true; }

  if (hasError) return;

  const interviewDate = vals.siDate;
  if (new Date(interviewDate) <= new Date()) {
    showFieldError("siDate", "Interview date must be a future date");
    return;
  }
  
  const body = {
    candidateId: parseInt(vals.siCand),
    jobDescriptionId: parseInt(vals.siJob),
    stage: vals.siStage,
    interviewDateTime: interviewDate,
    focusArea: vals.siFocus
  };

  try {
    const data = await scheduleInterviewAction(body);
    if (data.success) { toast("Interview scheduled!"); closeModal("scheduleModal"); loadInterviews(); }
    else { 
      const msgEl = document.getElementById("siMsg");
      msgEl.className = "msg error"; 
      msgEl.textContent = data.message || "Failed"; 
      msgEl.style.display = "block";
    }
  } catch(e) { toast("Server error","error"); }
}

async function openAssignPanel(id) {
  document.getElementById("apInterviewId").value = id;
  try {
    const data = await fetchPanels();
    const list = data.data || [];
    cachedPanels = list;
    const s1 = document.getElementById("apPanelId");
    const s2 = document.getElementById("apPanelId2");
    
    const options = '<option value="">-- Select Panel --</option>' + 
      list.map(p => {
        const displayName = p.name || p.fullName || p.email || 'Panel Member';
        return `<option value="${p.id}">${displayName} (${p.designation || 'N/A'})</option>`;
      }).join("");
    
    s1.innerHTML = options;
    s2.innerHTML = options;
    
    openModal("assignPanelModal");
  } catch(e) { toast("Error loading panels","error"); }
}

async function assignPanel(e) {
  e.preventDefault();
  const interviewId = parseInt(document.getElementById("apInterviewId").value);
  const panel1Id = document.getElementById("apPanelId").value;
  const panel1Focus = document.getElementById("apFocus").value;
  const panel2Id = document.getElementById("apPanelId2").value;
  const panel2Focus = document.getElementById("apFocus2") ? document.getElementById("apFocus2").value : "";

  if (panel1Id === panel2Id && panel1Id) {
    document.getElementById("apMsg").className="msg error"; 
    document.getElementById("apMsg").textContent="Cannot assign the same panel member twice.";
    return;
  }

  try {
    const p1Data = await assignPanelAction({ interviewId, panelId: parseInt(panel1Id), focusArea: panel1Focus });
    if (!p1Data.success) {
      document.getElementById("apMsg").className="msg error"; 
      document.getElementById("apMsg").textContent=p1Data.message||"Failed to assign Panel 1";
      return;
    }
    
    if (panel2Id) {
      const p2Data = await assignPanelAction({ interviewId, panelId: parseInt(panel2Id), focusArea: panel2Focus });
      if (!p2Data.success) {
        document.getElementById("apMsg").className="msg error"; 
        document.getElementById("apMsg").textContent=p2Data.message||"Failed to assign Panel 2";
        return;
      }
    }
    
    toast("Panel(s) assigned!"); closeModal("assignPanelModal"); loadInterviews();
  } catch(e) { toast("Server error","error"); }
}

window.viewFeedback = async function(intId) {
  const contentEl = document.getElementById("feedbackContent");
  contentEl.innerHTML = "<p class='empty-state'>Fetching feedback...</p>";
  openModal("feedbackModal");

  try {
    const data = await getFeedbackByInterview(intId);
    Renderers.renderFeedbackData(data.data, contentEl);
  } catch(err) {
    contentEl.innerHTML = "<p class='msg error'>Failed to load feedback.</p>";
  }
};

window.viewCandidateFeedback = async function(candId) {
  const contentEl = document.getElementById("feedbackContent");
  contentEl.innerHTML = "<p class='empty-state'>Fetching all candidate feedback...</p>";
  openModal("feedbackModal");

  try {
    const data = await getFeedbackByCandidate(candId);
    Renderers.renderFeedbackData(data.data, contentEl, true);
  } catch(err) {
    contentEl.innerHTML = "<p class='msg error'>Failed to load feedback history.</p>";
  }
};

window.openHRFeedback = function(id, name) {
  document.getElementById("hfIntId").value = id;
  openModal("hrFeedbackModal");
};

window.handleHRFeedback = async function(e) {
  e.preventDefault();
  const intId = parseInt(document.getElementById("hfIntId").value);
  const status = document.getElementById("hfStatus").value;
  const rating = parseInt(document.getElementById("hfRating").value);

  if (isNaN(rating) || rating < 1 || rating > 5) {
    toast("Rating must be between 1 and 5", "error");
    return;
  }
  
  const body = {
    interviewId: intId,
    panelId: null, // HR feedback
    rating: rating,
    status: status,
    comments: document.getElementById("hfComments").value,
    strengths: "N/A (HR Round)",
    weaknesses: "N/A (HR Round)",
    areasCovered: "Behavioral, Culture Fit"
  };
  
  try {
    const data = await submitFeedbackAction(body);
    if (data.success) {
      toast("HR Verdict Submitted!");
      closeModal("hrFeedbackModal");
      
      // Since it's the final round, also update candidate status automatically
      const interview = cachedInterviews.find(i => i.id === intId);
      if (interview && interview.candidateId) {
        await progressStageAction({ candidateId: interview.candidateId, newStage: status });
      }
      
      refreshAllData();
    } else {
      toast(data.message || "Failed to submit feedback", "error");
    }
  } catch(e) {
    toast("Server error", "error");
  }
};


window.scheduleNextRound = async function(candId) {
  // First open the progress modal to move them forward
  document.getElementById("spCandId").value = candId;
  openModal("stageModal");
  
  // When they submit the stage modal, it will refresh everything.
  // Then the user can just click "Schedule Interview" at the top.
  // Actually, let's make it even smoother.
  toast("Progress the candidate first, then schedule the new round!");
};
window.viewAssignmentDetails = function(jsonStr) {
  const data = JSON.parse(jsonStr);
  const contentEl = document.getElementById("assignmentDetailsContent");
  contentEl.innerHTML = data.names.map((name, idx) => `
    <div style="padding: 12px; background: #f8fafc; border-radius: 8px; margin-bottom: 10px; border: 1px solid #e2e8f0;">
      <div style="font-weight: 600; color: var(--text); margin-bottom: 4px;">👤 ${name}</div>
      <div style="font-size: 13px; color: var(--text-light);"><strong>Focus:</strong> ${data.focus[idx] || 'General Assessment'}</div>
    </div>
  `).join("");
  openModal("assignmentDetailsModal");
};

async function deleteInterview(id) {
  if (!confirm("Are you sure you want to DELETE this interview record permanently? This action cannot be undone and should only be used for correcting mistakes.")) return;
  try {
    const data = await deleteInterviewAction(id);
    if (data.success) { toast("Interview deleted"); loadInterviews(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}


window.openAddPanelModal = function() {
  document.getElementById("pId").value = "";
  document.getElementById("pName").value = "";
  document.getElementById("pEmail").value = "";
  document.getElementById("pOrg").value = "";
  document.getElementById("pDesig").value = "";
  document.getElementById("pMobile").value = "";
  document.getElementById("panelModalTitle").textContent = "Add Panel Member";
  document.getElementById("panelSubmitBtn").textContent = "Create";
  openModal("panelModal");
}

window.openEditPanelModal = function(id) {
  const p = cachedPanels.find(x => x.id === id);
  if (!p) return;
  document.getElementById("pId").value = p.id;
  document.getElementById("pName").value = p.name || "";
  document.getElementById("pEmail").value = p.email;
  document.getElementById("pOrg").value = p.organization;
  document.getElementById("pDesig").value = p.designation;
  document.getElementById("pMobile").value = p.mobileNumber;
  document.getElementById("panelModalTitle").textContent = "Edit Panel Member";
  document.getElementById("panelSubmitBtn").textContent = "Save Changes";
  openModal("panelModal");
}

window.handlePanelSubmit = async function(e) {
  e.preventDefault();
  clearErrors("panelForm");
  
  const id = document.getElementById("pId").value;
  const fullName = document.getElementById("pName").value.trim();
  const mobileNumber = document.getElementById("pMobile").value.trim();
  
  const body = {
    fullName: fullName,
    email: document.getElementById("pEmail").value.trim(),
    organization: document.getElementById("pOrg").value.trim(),
    designation: document.getElementById("pDesig").value.trim(),
    mobileNumber: mobileNumber
  };

  let hasError = false;

  // Name Validation: Only alphabets and spaces
  const nameRegex = /^[a-zA-Z\s.-]+$/;
  if (!nameRegex.test(fullName)) {
    showFieldError("pName", "Name should contain only alphabets and spaces.");
    hasError = true;
  }

  const mobileRegex = /^[0-9]{10}$/;
  if (!mobileRegex.test(mobileNumber)) {
    showFieldError("pMobile", "Mobile number must be exactly 10 digits.");
    hasError = true;
  }

  if (hasError) return;

  try {
    const data = id ? await updatePanelAction(id, body) : await createPanelAction(body);
    if (data.success) {
      toast(id ? "Panel updated" : "Panel created");
      closeModal("panelModal");
      loadPanels();
    } else {
      toast(data.message || "Failed", "error");
    }
  } catch (e) {
    toast("Server error", "error");
  }
}

async function deletePanelMember(id) {
  if (!confirm("Delete panel P-"+id+"?")) return;
  try {
    const data = await deletePanelAction(id);
    if (data.success) { toast("Panel deleted"); loadPanels(); } else toast(data.message||"Failed","error");
  } catch(e) { toast("Server error","error"); }
}

async function submitFeedback(e) {
  e.preventDefault();
  const msgEl = document.getElementById("gfMsg");
  msgEl.style.display = "none";
  const body = {
    interviewId: parseInt(document.getElementById("gfIntId").value),
    panelId: null, // HR feedback
    rating: parseInt(document.getElementById("gfRating").value),
    status: document.getElementById("gfStatus").value,
    comments: document.getElementById("gfComments").value,
    strengths: document.getElementById("gfStrengths").value,
    weaknesses: document.getElementById("gfWeaknesses").value,
    areasCovered: document.getElementById("gfAreas").value
  };

  if (isNaN(body.rating) || body.rating < 1 || body.rating > 5) {
    toast("Rating must be between 1 and 5", "error");
    return;
  }

  try {
    const data = await submitFeedbackAction(body);
    if (data.success) {
      toast("HR Feedback submitted!");
      closeModal("giveFeedbackModal");
      document.getElementById("giveFeedbackForm").reset();
      loadInterviews();
      loadFeedbackCenter();
    } else {
      msgEl.className = "msg error";
      msgEl.textContent = data.message || "Failed to submit feedback";
      msgEl.style.display = "block";
    }
  } catch(e) { toast("Server error", "error"); }
}

// Form Cleanups
document.getElementById("searchJobs")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedJobs.filter(j => 
        j.title.toLowerCase().includes(q) || 
        j.location.toLowerCase().includes(q) ||
        j.id.toString().includes(q)
    );
    Renderers.renderJobs(filtered);
});

document.getElementById("searchInterviews")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedInterviews.filter(i => 
        (i.candidateName || '').toLowerCase().includes(q) || 
        (i.stage || '').toLowerCase().includes(q) ||
        (i.status || '').toLowerCase().includes(q) ||
        i.id.toString().includes(q)
    );
    Renderers.renderInterviews(filtered);
});

document.getElementById("searchJobs")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedJobs.filter(j => 
        (j.title || '').toLowerCase().includes(q) || 
        (j.location || '').toLowerCase().includes(q) ||
        (j.skills || '').toLowerCase().includes(q)
    );
    Renderers.renderJobs(filtered);
});

document.getElementById("searchInterviews")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedInterviews.filter(i => 
        (i.candidateName || '').toLowerCase().includes(q) || 
        (i.jobTitle || '').toLowerCase().includes(q) ||
        (i.stage || '').toLowerCase().includes(q) ||
        (i.status || '').toLowerCase().includes(q)
    );
    Renderers.renderInterviews(filtered);
});

document.getElementById("searchPanels")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedPanels.filter(p => 
        (p.name || '').toLowerCase().includes(q) || 
        (p.email || '').toLowerCase().includes(q) ||
        (p.organization || '').toLowerCase().includes(q)
    );
    Renderers.renderPanels(filtered);
});

document.getElementById("searchFeedback")?.addEventListener("input", (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = cachedFeedback.filter(f => 
        (f.candidateName || '').toLowerCase().includes(q) || 
        (f.interviewStage || '').toLowerCase().includes(q) ||
        (f.status || '').toLowerCase().includes(q) ||
        (f.panelName || '').toLowerCase().includes(q)
    );
    Renderers.renderFeedbackCenter(filtered);
});


window.toast = toast;
window.openModal = openModal;
window.closeModal = closeModal;
window.logout = logout;
window.showTab = showTab;
window.openStageModal = openStageModal;
window.progressStage = progressStage;
window.deleteCandidate = deleteCandidate;
window.openAssignPanel = openAssignPanel;
window.assignPanel = assignPanel;
window.handleUpdateStatus = handleUpdateStatus;
window.openScheduleModal = openScheduleModal;
window.scheduleInterview = scheduleInterview;
window.deleteInterview = deleteInterview;
window.openEditJobModal = openEditJobModal;
window.updateJob = updateJob;
window.activateJob = activateJob;
window.deactivateJob = deactivateJob;
window.createJob = createJob;
window.deletePanelMember = deletePanelMember;
window.applyFilters = applyFilters;
window.resetFilters = resetFilters;
window.populateJDFilter = populateJDFilter;
window.submitFeedback = submitFeedback;
window.loadActivityFeed = loadActivityFeed;

/**
 * Fetches data for the top-level dashboard metrics and renders them.
 */
async function loadDashboardStats() {
    try {
        let interviews = [], candidates = [], jobs = [];
        
        try { interviews = (await fetchInterviews()).data || []; } catch(e) {}
        try { candidates = (await getAllCandidates()).data || []; } catch(e) {}
        try { jobs = (await fetchAllJobs()).data || []; } catch(e) {}
        
        Renderers.renderDashboardStats(interviews, candidates, jobs);
    } catch(e) { console.error("loadDashboardStats error:", e); }
}

document.addEventListener("DOMContentLoaded", () => {
    ["createJobForm", "editJobForm", "scheduleForm", "panelForm", "assignPanelForm"].forEach(initFormCleanup);
    renderSidebarProfile();
    showTab("candidates");
    loadDashboardStats();
    loadActivityFeed();
});
