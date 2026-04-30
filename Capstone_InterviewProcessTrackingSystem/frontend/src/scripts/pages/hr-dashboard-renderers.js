/**
 * UI Renderers for the HR Dashboard.
 * Separates HTML structure from business logic to maintain Separation of Concerns.
 */

import { APPLICATION_STATUS } from "../constants/index.js";

/**
 * Renders the Jobs grid.
 */
export function renderJobs(list) {
    const grid = document.getElementById("jobsGrid");
    if (!grid) return;

    // Render Stats
    const activeCount = list.filter(j => j.isActive !== false).length;
    const statsContainer = document.getElementById("jobStats");
    if (statsContainer) {
        statsContainer.innerHTML = `
          <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg></div><div><div class="stat-value">${activeCount}</div><div class="stat-label">Active Jobs</div></div></div>
          <div class="stat-card"><div class="stat-icon" style="background:#f3f4f6"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#6b7280" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg></div><div><div class="stat-value">${list.length}</div><div class="stat-label">Total Jobs</div></div></div>`;
    }

    if (!list.length) { grid.innerHTML = '<div class="empty-state">No jobs found.</div>'; return; }
    
    grid.innerHTML = list.map(j => `
      <div class="job-card ${j.isActive === false ? 'job-inactive' : ''}">
        <div class="job-header">
          <h3 class="job-title">${j.title}</h3>
          <div class="dropdown">
            <button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>
            <div class="dropdown-content">
              <button onclick="openEditJobModal(${j.id})">Edit Details</button>
              ${j.isActive === false ? 
                `<button onclick="activateJob(${j.id})">Activate Job</button>` : 
                `<button style="color: var(--danger)" onclick="deactivateJob(${j.id})">Deactivate Job</button>`}
            </div>
          </div>
        </div>
        <div class="job-id-row" style="margin-bottom: 12px;">
          <span class="job-id">J-${j.id}</span>
          <span class="badge ${j.isActive === false ? 'badge-danger' : 'badge-success'}">${j.isActive === false ? 'Inactive' : 'Active'}</span>
        </div>
        <div class="job-detail">
          <svg viewBox="0 0 24 24" width="16" height="16"><path fill="#64748b" d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/></svg>
          ${j.location}
        </div>
        <div class="job-detail">
          <svg viewBox="0 0 24 24" width="16" height="16"><path fill="#64748b" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>
          ${j.minExperience}-${j.maxExperience} yrs
        </div>
        <div class="job-detail">
          <svg viewBox="0 0 24 24" width="16" height="16"><path fill="#64748b" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/></svg>
          ${j.minSalary ? j.minSalary + '-' + j.maxSalary + ' LPA' : 'Not specified'}
        </div>
        <span class="job-type-badge">${(j.jobType || 'FULL_TIME').replace(/_/g, ' ')}</span>
      </div>
    `).join("");
}

/**
 * Renders the Interviews table.
 */
export function renderInterviews(list) {
    const body = document.getElementById("intBody");
    if (!body) return;

    // Render Stats
    const statsContainer = document.getElementById("intStats");
    if (statsContainer) {
        statsContainer.innerHTML = `
          <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/></svg></div><div><div class="stat-value">${list.length}</div><div class="stat-label">Total</div></div></div>`;
    }

    if (!list.length) { body.innerHTML = '<tr><td colspan="7" class="empty-state">No interviews found.</td></tr>'; return; }
    
    body.innerHTML = list.map(i => {
      const panelNames = i.assignedPanelNames || [];
      const count = panelNames.length;
      const panelInfoJson = JSON.stringify({ names: panelNames, focus: i.assignedPanelFocusAreas || [] }).replace(/"/g, '&quot;');
      const isHRStage = i.stage === 'HR';
      
      const panelHtml = isHRStage 
        ? '<span class="badge badge-info">👤 HR Internal</span>'
        : (count === 0 
            ? '<span class="badge badge-warning">⚠️ Not Assigned</span>' 
            : (count === 1 
                ? `<span class="badge badge-info" style="cursor:pointer" onclick="viewAssignmentDetails('${panelInfoJson}')">👤 ${panelNames[0]}</span>`
                : `<span class="badge badge-success" style="cursor:pointer" onclick="viewAssignmentDetails('${panelInfoJson}')">👥 ${count} Assigned</span>`));

      const statusBadge = i.status === 'COMPLETED' ? 'badge-success' : 
                         (i.status === 'CANCELLED' || i.status === 'NO_SHOW' ? 'badge-danger' : 'badge-warning');

      const hrFeedbackGiven = i.feedbackProvidedBy && i.feedbackProvidedBy.includes(null);
      const canGiveFeedback = (isHRStage && !hrFeedbackGiven) || (!isHRStage && (i.assignedPanelIds || []).length > 0);

      return `<tr>
        <td><strong>I-${i.id}</strong></td>
        <td><strong>${i.candidateName || 'N/A'}</strong></td>
        <td><span class="badge badge-info">${i.stage || 'N/A'}</span></td>
        <td>${i.interviewDateTime ? new Date(i.interviewDateTime).toLocaleString() : 'TBD'}</td>
        <td><span class="badge ${statusBadge}">${i.status || 'SCHEDULED'}</span></td>
        <td>${panelHtml}</td>
        <td>
          <div class="actions-cell"><div class="dropdown">
            <button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>
            <div class="dropdown-content">
              ${(i.status !== 'COMPLETED' && i.status !== 'CANCELLED') ? 
                `<button onclick="openAssignPanel(${i.id}, '${i.stage}')">Assign Panel</button>
                 <button onclick="openUpdateStatus(${i.id})">Update Status</button>` : ''}
              ${(i.status === 'COMPLETED' || i.status === 'EVALUATED') ? 
                `<button onclick="viewFeedback(${i.id})">View Feedback</button>` : ''}
              ${canGiveFeedback ? `<button onclick="openGiveFeedback(${i.id}, '${i.candidateName}', ${i.candidateId})">Give Feedback</button>` : ''}
              <button style="color:var(--danger)" onclick="deleteInterview(${i.id})">Cancel Interview</button>
            </div>
          </div></div>
        </td>
      </tr>`;
    }).join("");
}

/**
 * Renders the Panel Members table.
 */
export function renderPanels(list) {
    const body = document.getElementById("panelsBody");
    if (!body) return;
    if (!list.length) { body.innerHTML = '<tr><td colspan="7" class="empty-state">No panels found.</td></tr>'; return; }
    body.innerHTML = list.map(p => `<tr>
        <td><strong>P-${p.id}</strong></td>
        <td>${p.name || 'N/A'}</td>
        <td>${p.email}</td>
        <td>${p.organization}</td>
        <td>${p.designation}</td>
        <td>${p.mobileNumber}</td>
        <td>
            <button class="btn btn-sm btn-outline" onclick="openEditPanelModal(${p.id})">Edit</button>
            <button class="btn btn-sm btn-danger" onclick="deletePanelMember(${p.id})">Delete</button>
        </td>
    </tr>`).join("");
}

/**
 * Renders the Feedback Center grid.
 */
export function renderFeedbackCenter(list) {
    const grid = document.getElementById("fbCenterGrid");
    if (!grid) return;
    if (!list.length) {
      grid.innerHTML = '<div class="empty-state">No feedback found matching search.</div>';
      return;
    }
    grid.innerHTML = list.map(f => {
      const rating = f.rating || 0;
      const decisionClass = f.status === 'SELECTED' ? 'decision-selected' : 'decision-rejected';
      
      return `
      <div class="evaluation-card">
        <div class="eval-header">
          <div class="eval-candidate-info">
            <h3>${f.candidateName || 'N/A'}</h3>
            <p>ID: C-${f.candidateId}</p>
          </div>
          <div class="eval-meta">
            <span class="eval-round-badge">${f.interviewStage || 'N/A'}</span>
          </div>
        </div>
        <div class="eval-body">
          <div class="eval-score-row">
            <div class="eval-stars">${'★'.repeat(rating)}${'☆'.repeat(5-rating)}</div>
            <div class="decision-badge ${decisionClass}">${f.status}</div>
          </div>
          
          <div class="eval-section">
            <span class="eval-section-label">Technical Strengths</span>
            <div class="eval-text-box eval-box-strengths">
              ${f.strengths || 'No specific strengths noted.'}
            </div>
          </div>
          
          <div class="eval-section">
            <span class="eval-section-label">Areas for Improvement</span>
            <div class="eval-text-box eval-box-weaknesses">
              ${f.weaknesses || 'No specific weaknesses identified.'}
            </div>
          </div>

          <div class="eval-section">
            <span class="eval-section-label">Overall Comments</span>
            <div class="eval-text-box eval-box-comments">
              ${f.comments || 'No additional comments provided.'}
            </div>
          </div>
          
          <div class="eval-footer">
            <div class="eval-panel-info">
               <span class="eval-panel-name">Evaluated By: <strong>${f.panelName || 'HR'}</strong></span>
            </div>
          </div>
        </div>
      </div>`;
    }).join("");
}

/**
 * Renders the Activity Feed timeline.
 */
export function renderActivityFeed(activities) {
    const feedContainer = document.getElementById("activityFeed");
    if (!feedContainer) return;
    if (!activities.length) {
        feedContainer.innerHTML = '<div class="empty-state">No recent activity.</div>';
        return;
    }

    feedContainer.innerHTML = activities.slice(0, 6).map(act => `
        <div style="display: flex; gap: 12px; padding: 12px; border-radius: 8px; background: #f8fafc; border-left: 4px solid ${act.color};">
            <div style="font-size: 1.5rem;">${act.icon}</div>
            <div>
                <div style="font-weight: 600; font-size: 0.9rem; color: var(--text);">${act.title}</div>
                <div style="font-size: 0.85rem; color: var(--text-secondary); margin: 2px 0;">${act.desc}</div>
                <div style="font-size: 0.7rem; color: #94a3b8;">Ref ID: #${act.time}</div>
            </div>
        </div>
    `).join("");
}

/**
 * Renders the Candidates table.
 */
export function renderCandidates(list) {
    const body = document.getElementById("candBody");
    if (!body) return;

    // Render Stats
    const statsContainer = document.getElementById("candStats");
    if (statsContainer) {
        const total = list.length,
              selected = list.filter(c => c.applicationStatus === APPLICATION_STATUS.SELECTED).length,
              rejected = list.filter(c => c.applicationStatus === APPLICATION_STATUS.REJECTED).length,
              inProgress = total - selected - rejected;
              
        statsContainer.innerHTML = `
          <div class="stat-card"><div class="stat-icon" style="background:#dbeafe"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#3b82f6" d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg></div><div><div class="stat-value">${total}</div><div class="stat-label">Total</div></div></div>
          <div class="stat-card"><div class="stat-icon" style="background:#fef3c7"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#f59e0b" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg></div><div><div class="stat-value">${inProgress}</div><div class="stat-label">In Progress</div></div></div>
          <div class="stat-card"><div class="stat-icon" style="background:#d1fae5"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg></div><div><div class="stat-value">${selected}</div><div class="stat-label">Selected</div></div></div>
          <div class="stat-card"><div class="stat-icon" style="background:#fee2e2"><svg class="stat-svg" viewBox="0 0 24 24"><path fill="#ef4444" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg></div><div><div class="stat-value">${rejected}</div><div class="stat-label">Rejected</div></div></div>`;
    }
    
    if (list.length === 0) {
      body.innerHTML = '<tr><td colspan="6" class="empty-state">No candidates found matching filters.</td></tr>';
      return;
    }

    body.innerHTML = list.map(c => {
      const statusClass = c.applicationStatus === APPLICATION_STATUS.SELECTED || c.applicationStatus === APPLICATION_STATUS.EVALUATED ? 'badge-success' : 
                         (c.applicationStatus === APPLICATION_STATUS.REJECTED ? 'badge-danger' : 
                         (c.applicationStatus === APPLICATION_STATUS.PROFILING_COMPLETED ? 'badge-warning' : 'badge-info'));
      
      let statusText = c.applicationStatus.replace(/_/g, ' ');
      if (c.applicationStatus === APPLICATION_STATUS.INTERVIEW_SCHEDULED) statusText = 'SCHEDULED';
      if (c.applicationStatus === APPLICATION_STATUS.PROFILING_COMPLETED) statusText = 'READY TO SCHEDULE';

      return `<tr>
        <td><strong>C-${c.id}</strong></td>
        <td>${c.fullName || 'N/A'}</td>
        <td>${c.jobTitle || 'N/A'}</td>
        <td><span class="badge badge-info">${c.currentStage || 'N/A'}</span></td>
        <td><span class="badge ${statusClass}">${statusText || 'N/A'}</span></td>
        <td>
          <div class="actions-cell"><div class="dropdown">
            <button class="btn-icon"><svg viewBox="0 0 24 24" width="20" height="20"><path fill="currentColor" d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg></button>
            <div class="dropdown-content">
              <button onclick="openStageModal(${c.id})">Progress Stage</button>
              <button onclick="viewCandidateFeedback(${c.id})">View All Feedbacks</button>
              ${c.resumeUrl ? `<a href="${c.resumeUrl}" target="_blank">View Resume</a>` : ''}
              <button style="color: var(--danger)" onclick="deleteCandidate(${c.id})">Delete Candidate</button>
            </div>
          </div></div>
        </td>
      </tr>`;
    }).join("");
}

/**
 * Renders the Feedback data for modals.
 */
export function renderFeedbackData(list, container, showRound = false) {
  if (!list || list.length === 0) {
    container.innerHTML = "<div class='empty-state'><p>No feedback submitted yet.</p></div>";
    return;
  }
  container.innerHTML = list.map(f => {
    const rating = f.rating || 0;
    const decisionClass = f.status === 'SELECTED' ? 'decision-selected' : 'decision-rejected';
    
    return `
    <div class="evaluation-card" style="margin-bottom: 20px; box-shadow: none; border: 1px solid #f1f5f9;">
      <div class="eval-header" style="padding: 12px 16px;">
        <div class="eval-candidate-info">
          <h4 style="font-size: 0.95rem; margin: 0;">${f.panelName || 'HR Manager'}</h4>
          <p style="margin: 0; font-size: 0.75rem;">${showRound ? 'Round ID: #'+f.id : 'Evaluation #F-'+f.id}</p>
        </div>
        <div class="eval-stars" style="font-size: 1rem;">${'★'.repeat(rating)}${'☆'.repeat(5-rating)}</div>
      </div>
      <div class="eval-body" style="padding: 16px;">
        <div class="eval-section">
          <span class="eval-section-label">Strengths</span>
          <div class="eval-text-box eval-box-strengths" style="font-size: 0.8rem; padding: 8px;">${f.strengths || 'N/A'}</div>
        </div>
        <div class="eval-section">
          <span class="eval-section-label">Weaknesses</span>
          <div class="eval-text-box eval-box-weaknesses" style="font-size: 0.8rem; padding: 8px;">${f.weaknesses || 'N/A'}</div>
        </div>
        <div class="eval-section" style="margin-bottom: 0;">
          <span class="eval-section-label">Comments</span>
          <div class="eval-text-box eval-box-comments" style="font-size: 0.8rem; padding: 8px;">${f.comments || 'N/A'}</div>
        </div>
      </div>
      <div class="eval-footer" style="padding: 8px 16px;">
        <span class="decision-badge ${decisionClass}">${f.status}</span>
      </div>
    </div>`;
  }).join("");
}

/**
 * Renders the top-level dashboard statistics (counts for Jobs, Candidates, Interviews).
 */
export function renderDashboardStats(interviews, candidates, jobs) {
    const statsContainer = document.getElementById("dashboardStats");
    if (!statsContainer) return;

    const stats = {
        totalJobs: jobs.length,
        activeJobs: jobs.filter(j => j.isActive !== false).length,
        totalCandidates: candidates.length,
        selectedCandidates: candidates.filter(c => c.applicationStatus === 'SELECTED').length,
        pendingInterviews: interviews.filter(i => i.status === 'SCHEDULED' || i.status === 'IN_PROGRESS').length
    };

    statsContainer.innerHTML = `
        <div class="stat-card">
            <div class="stat-icon" style="background: #dbeafe;">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="#3b82f6" d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-2 .89-2 2v11c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>
            </div>
            <div>
                <div class="stat-value">${stats.activeJobs} / ${stats.totalJobs}</div>
                <div class="stat-label">Active Jobs</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background: #fef3c7;">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="#f59e0b" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm4.59-12.42L10 14.17l-2.59-2.58L6 13l4 4 8-8z"/></svg>
            </div>
            <div>
                <div class="stat-value">${stats.totalCandidates}</div>
                <div class="stat-label">Total Candidates</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background: #d1fae5;">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>
            </div>
            <div>
                <div class="stat-value">${stats.selectedCandidates}</div>
                <div class="stat-label">Hired</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background: #fee2e2;">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="#ef4444" d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm-.5-13h1v6l5.25 3.15-.75 1.23-6-3.6V7z"/></svg>
            </div>
            <div>
                <div class="stat-value">${stats.pendingInterviews}</div>
                <div class="stat-label">Pending Interviews</div>
            </div>
        </div>
    `;
}
