import { getMe } from "../actions/auth.js";
import { createCandidate, uploadFile, getMyProfile } from "../actions/candidate.js";

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

// Pre-fill User Info and Check if profile already exists
async function init() {
  try {
    // 1. Check if they already applied
    const profileRes = await getMyProfile();
    if (profileRes.success && profileRes.data) {
      // Disable form and show message
      const btn = document.querySelector('button[type="submit"]');
      if (btn) {
        btn.disabled = true;
        btn.textContent = "Application Already Exists";
        btn.style.opacity = "0.5";
        btn.style.cursor = "not-allowed";
      }
      
      const msgEl = document.getElementById("apMsg");
      if (msgEl) {
        msgEl.className = "msg info";
        msgEl.textContent = "You already have an active application. Please visit your Dashboard to see the status.";
        msgEl.style.display = "block";
      }
      return;
    }

    // 2. Not applied yet, fetch user details to pre-fill
    const userData = await getMe();
    if (userData.success && userData.data) {
      const user = userData.data;
      const fullNameEl = document.getElementById("apFullName");
      const emailEl = document.getElementById("apEmail");
      const mobileEl = document.getElementById("apMobile");

      if (fullNameEl) {
        fullNameEl.value = user.fullName || "";
        fullNameEl.readOnly = true;
        fullNameEl.style.backgroundColor = "#f3f4f6";
      }
      if (emailEl) {
        emailEl.value = user.email || "";
        emailEl.readOnly = true;
        emailEl.style.backgroundColor = "#f3f4f6";
      }
      if (mobileEl) {
        mobileEl.value = user.mobileNumber || "";
      }
    }
  } catch (err) {
    console.error("Init failed", err);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  init();

  const applyForm = document.getElementById("applyForm");
  if (applyForm) {
    applyForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const msgEl = document.getElementById("apMsg");
      if (msgEl) msgEl.style.display = "none";

      const totalExpVal = document.getElementById("apExp")?.value || "0";
      const relExpVal = document.getElementById("apRelExp")?.value || "0";
      const totalExp = parseInt(totalExpVal);
      const relExp = parseInt(relExpVal);

      // Frontend Validation: Relevant <= Total
      if (relExp > totalExp) {
        if (msgEl) {
          msgEl.className = "msg error";
          msgEl.textContent = "Relevant experience cannot be greater than total experience.";
          msgEl.style.display = "block";
        }
        return;
      }

      try {
        // 1. Upload Resume
        const fileInput = document.getElementById("apResumeFile");
        const file = fileInput?.files?.[0];
        if (!file) {
          if (msgEl) {
            msgEl.className = "msg error";
            msgEl.textContent = "Please upload a resume.";
            msgEl.style.display = "block";
          }
          return;
        }

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
          userId: parseInt(localStorage.getItem("userId") || "0"),
          jobId: jobId ? parseInt(jobId) : null,
          source: "CAREER_PORTAL",
          resumeUrl: uploadData.data.url,
          mobileNumber: document.getElementById("apMobile")?.value.trim(),
          totalExperience: totalExp,
          relevantExperience: relExp,
          currentCompany: document.getElementById("apCompany")?.value.trim(),
          preferredLocation: document.getElementById("apLocation")?.value.trim(),
          currentCTC: parseFloat(document.getElementById("apCurrentCtc")?.value || "0"),
          expectedCTC: parseFloat(document.getElementById("apExpectedCtc")?.value || "0"),
          noticePeriod: parseInt(document.getElementById("apNotice")?.value || "0")
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
