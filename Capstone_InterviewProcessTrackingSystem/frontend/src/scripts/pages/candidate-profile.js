import { getMe } from "../actions/auth.js";
import { createCandidate, uploadFile, getMyProfile } from "../actions/candidate.js";
import { showFieldError, clearErrors } from "../lib/utils/ui.js";

const params = new URLSearchParams(window.location.search);
const jobId = params.get("jobId");
const jobTitle = params.get("jobTitle");

if (jobTitle) {
  const titleEl = document.getElementById("applyTitle");
  if (titleEl) titleEl.textContent = "Apply: " + decodeURIComponent(jobTitle);
}

const token = localStorage.getItem("token");
if (!token) {
  alert("Please login first to apply");
  window.location.href = "sign-in/index.html";
}

window.logout = function() {
  localStorage.clear();
  window.location.href = "sign-in/index.html";
};

let currentUser = null;

// Pre-fill User Info and Check if profile already exists
async function init() {
  const fullNameEl = document.getElementById("apFullName");
  const emailEl = document.getElementById("apEmail");
  const mobileEl = document.getElementById("apMobile");

  const applyLock = (el, val) => {
    if (!el || !val) return;
    el.value = val;
    el.readOnly = true;
    el.style.backgroundColor = "#f3f4f6";
    el.style.color = "#64748b";
    el.style.cursor = "not-allowed";
    el.style.pointerEvents = "none";
    el.classList.add("locked-input");
  };

  // 1. Instant Lock from LocalStorage
  const cachedName = localStorage.getItem("userName");
  const cachedEmail = localStorage.getItem("email");
  const cachedMobile = localStorage.getItem("mobileNumber");
  
  applyLock(fullNameEl, cachedName);
  applyLock(emailEl, cachedEmail);
  applyLock(mobileEl, cachedMobile);

  try {
    // 2. Sync with Backend
    const userData = await getMe();
    if (userData.success && userData.data) {
      currentUser = userData.data;
      applyLock(fullNameEl, currentUser.fullName);
      applyLock(emailEl, currentUser.email);
      applyLock(mobileEl, currentUser.mobileNumber);
    }

    // 3. Check if they already applied
    let profileRes = null;
    try {
      profileRes = await getMyProfile();
    } catch (e) { }

    if (profileRes && profileRes.success && profileRes.data) {
      const btn = document.querySelector('button[type="submit"]');
      if (btn) {
        btn.disabled = true;
        btn.textContent = "Application Already Exists";
        btn.style.opacity = "0.5";
      }
      const msgEl = document.getElementById("apMsg");
      if (msgEl) {
        msgEl.className = "msg info";
        msgEl.textContent = "You already have an active application.";
        msgEl.style.display = "block";
      }
    }
  } catch (err) {
    console.error("Init failed:", err);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  init();

  const applyForm = document.getElementById("applyForm");
  if (applyForm) {
    applyForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      clearErrors("applyForm");
      
      const msgEl = document.getElementById("apMsg");
      if (msgEl) msgEl.style.display = "none";

      const totalExpVal = document.getElementById("apExp")?.value || "0";
      const relExpVal = document.getElementById("apRelExp")?.value || "0";
      const totalExp = parseInt(totalExpVal);
      const relExp = parseInt(relExpVal);

      let hasError = false;

      // Frontend Validation: Relevant <= Total
      if (relExp > totalExp) {
        showFieldError("apRelExp", "Relevant experience cannot be greater than total experience.");
        hasError = true;
      }

      // Mobile Number Validation: Exactly 10 digits, numbers only
      const mobileNumber = document.getElementById("apMobile")?.value.trim() || "";
      const mobileRegex = /^[0-9]{10}$/;
      if (!mobileRegex.test(mobileNumber)) {
        showFieldError("apMobile", "Mobile number must be exactly 10 digits.");
        hasError = true;
      }

      // Check Resume
      const fileInput = document.getElementById("apResumeFile");
      const file = fileInput?.files?.[0];
      if (!file) {
        showFieldError("apResumeFile", "Please upload a resume (PDF).");
        hasError = true;
      }

      if (hasError) return;

      try {
        // 1. Upload Resume
        const formData = new FormData();
        formData.append("file", file);
        
        if (msgEl) {
          msgEl.className = "msg info";
          msgEl.textContent = "Uploading resume...";
          msgEl.style.display = "block";
        }
        
        const uploadData = await uploadFile(formData);
        
        if (!uploadData.success) {
          if (msgEl) {
            msgEl.className = "msg error";
            msgEl.textContent = uploadData.message || "Failed to upload resume";
            msgEl.style.display = "block";
          }
          return;
        }

        // 2. Submit Application
        const body = {
          userId: currentUser.id,
          jobId: parseInt(jobId),
          mobileNumber: document.getElementById("apMobile").value.trim(),
          totalExperience: parseFloat(document.getElementById("apExp").value),
          relevantExperience: parseFloat(document.getElementById("apRelExp").value) || 0,
          currentCompany: document.getElementById("apCompany").value.trim(),
          currentCTC: parseFloat(document.getElementById("apCurrentCtc").value) || 0,
          expectedCTC: parseFloat(document.getElementById("apExpectedCtc").value) || 0,
          noticePeriod: parseInt(document.getElementById("apNotice").value) || 0,
          preferredLocation: document.getElementById("apLocation").value.trim(),
          resumeUrl: uploadData.data.url
        };

        const data = await createCandidate(body);
        if (data.success) {
          if (msgEl) {
            msgEl.className = "msg success";
            msgEl.textContent = "Application submitted successfully! Redirecting to dashboard...";
            msgEl.style.display = "block";
          }
          setTimeout(() => window.location.href = "dashboard.html", 1500);
        } else {
          if (msgEl) {
            msgEl.className = "msg error";
            msgEl.textContent = data.message || "Submission failed";
            msgEl.style.display = "block";
          }
        }
      } catch (err) {
        if (msgEl) {
          msgEl.className = "msg error";
          msgEl.textContent = "Server error. Please check your network or try again later.";
          msgEl.style.display = "block";
        }
      }
    });
  }
});
