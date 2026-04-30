import { SITE_CONFIG } from "../config/site-config.js";
import { fetchHandler } from "../lib/handlers/fetch.js";

/**
 * Fetches all active job descriptions and renders them on the page.
 */
async function loadJobs() {
  try {
    const res = await fetchHandler("/jobs");
    const jobs = res.data || [];

    document.getElementById("jobsCount").textContent = jobs.length + " Open Positions";

    if (!jobs.length) {
      document.getElementById("jobsGrid").innerHTML = `
        <div class="empty-message">
          <svg class="empty-svg" viewBox="0 0 24 24"><path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>
          <p>No jobs available right now. Check back later!</p>
        </div>`;
      return;
    }

    document.getElementById("jobsGrid").innerHTML = jobs.map(j => `
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
        ${j.description ? '<p class="job-desc">' + j.description + '</p>' : ''}
        ${j.skills && j.skills.length ? '<div class="job-skills">' + j.skills.map(s => '<span class="skill-tag">' + s + '</span>').join('') + '</div>' : ''}
        ${j.minSalary ? '<div class="job-salary"><svg viewBox="0 0 24 24" width="16" height="16"><path fill="#059669" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/></svg> ' + j.minSalary + ' - ' + j.maxSalary + ' LPA</div>' : ''}
        <span class="job-type-badge">${(j.jobType || 'FULL_TIME').replace(/_/g, ' ')}</span>
        <button onclick="applyForJob(${j.id}, '${j.title.replace(/'/g, "\\'")}')" class="btn-apply">Apply Now</button>
      </div>
    `).join("");
  } catch (e) {
    document.getElementById("jobsCount").textContent = "0 Open Positions";
    document.getElementById("jobsGrid").innerHTML = `
      <div class="empty-message">
        <svg class="empty-svg" viewBox="0 0 24 24"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
        <p>Unable to load jobs. Please make sure the backend server is running.</p>
      </div>`;
  }
}

/**
 * Redirects the user to the application page for a specific job.
 * If not logged in, redirects to the sign-in page first.
 * 
 * @param {number} jobId - The ID of the job being applied for.
 * @param {string} jobTitle - The title of the job.
 */
window.applyForJob = function(jobId, jobTitle) {
  const token = localStorage.getItem("token");
  const url = `candidate-profile.html?jobId=${jobId}&jobTitle=${encodeURIComponent(jobTitle)}`;
  if (!token) {
    localStorage.setItem("redirectAfterLogin", url);
    window.location.href = `sign-in/index.html`;
  } else {
    window.location.href = url;
  }
};

document.addEventListener("DOMContentLoaded", loadJobs);
