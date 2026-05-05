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
    // 2. Sync with Backend (with LocalStorage Failsafe)
    const userData = await getMe();
    if (userData.success && userData.data) {
      currentUser = userData.data;
    } else {
      // FALLBACK: Use data from LocalStorage if API fails
      console.warn("Backend profile fetch failed, using local storage fallback.");
      const storedId = localStorage.getItem("userId");
      if (storedId) {
        currentUser = {
          id: parseInt(storedId),
          fullName: localStorage.getItem("userName"),
          email: localStorage.getItem("email"),
          mobileNumber: localStorage.getItem("mobileNumber")
        };
      }
    }

    if (currentUser) {
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
  const totalExpInput = document.getElementById("apExp");
  const relExpInput = document.getElementById("apRelExp");
  const currentCtcInput = document.getElementById("apCurrentCtc");
  const expectedCtcInput = document.getElementById("apExpectedCtc");
  const mobileInput = document.getElementById("apMobile");
  const resumeInput = document.getElementById("apResumeFile");

  // Helper to show/clear required errors
  const checkRequired = (el, msg = "This field is required") => {
    if (!el || !el.value || el.value.trim() === "") {
      showFieldError(el ? el.id : "", msg);
      return false;
    }
    return true;
  };

  // Real-time Validation Logic
  const validateForm = () => {
    clearErrors("applyForm");
    let isValid = true;

    // 1. Required Fields
    if (!checkRequired(totalExpInput, "Total experience is required")) isValid = false;
    if (!checkRequired(mobileInput, "Mobile number is required")) isValid = false;
    
    // 2. Experience Logic
    const total = parseInt(totalExpInput?.value || "0");
    const rel = parseInt(relExpInput?.value || "0");
    if (rel > total) {
      showFieldError("apRelExp", "Relevant experience cannot exceed total experience.");
      isValid = false;
    }

    // 3. CTC Logic (Warning if Expected < Current)
    const current = parseFloat(currentCtcInput?.value || "0");
    const expected = parseFloat(expectedCtcInput?.value || "0");
    if (expected > 0 && expected < current) {
      showFieldError("apExpectedCtc", "Expected CTC is usually higher than current CTC.");
    }

    // 4. Mobile Format
    const mobileRegex = /^[0-9]{10}$/;
    if (mobileInput && mobileInput.value && !mobileRegex.test(mobileInput.value)) {
      showFieldError("apMobile", "Mobile number must be exactly 10 digits.");
      isValid = false;
    }

    return isValid;
  };

  // Attach real-time listeners
  [totalExpInput, relExpInput, currentCtcInput, expectedCtcInput, mobileInput].forEach(el => {
    el?.addEventListener("input", validateForm);
  });

  if (applyForm) {
    applyForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      
      const isValid = validateForm();
      const file = resumeInput?.files?.[0];

      if (!file) {
        showFieldError("apResumeFile", "Please upload your resume (PDF).");
        return;
      }

      if (!isValid) return;

      const msgEl = document.getElementById("apMsg");
      if (msgEl) msgEl.style.display = "none";

      if (!currentUser) {
        if (msgEl) {
          msgEl.className = "msg error";
          msgEl.textContent = "User information not loaded yet. Please wait a moment.";
          msgEl.style.display = "block";
        }
        return;
      }

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
          totalExperience: parseInt(document.getElementById("apExp").value),
          relevantExperience: parseInt(document.getElementById("apRelExp").value) || 0,
          currentCompany: document.getElementById("apCompany").value.trim(),
          currentCTC: parseFloat(document.getElementById("apCurrentCtc").value) || 0,
          expectedCTC: parseFloat(document.getElementById("apExpectedCtc").value) || 0,
          noticePeriod: parseInt(document.getElementById("apNotice").value) || 0,
          preferredLocation: document.getElementById("apLocation").value.trim(),
          source: document.getElementById("apSource").value,
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
          // Try to show the actual error from the backend if available
          const errorMessage = err.message || "Server error. Please check your network or try again later.";
          msgEl.textContent = errorMessage;
          msgEl.style.display = "block";
        }
      }
    });
  }
});
