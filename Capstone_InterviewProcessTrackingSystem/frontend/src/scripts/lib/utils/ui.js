import { SITE_CONFIG } from "../../config/site-config.js";

/**
 * Constructs a full URL for a resume, prepending the API URL if necessary.
 * @param {string} url - The relative or absolute resume URL.
 * @returns {string|null} The absolute URL or null.
 */
export function getResumeUrl(url) {
    if (!url) return null;
    if (url.startsWith('http')) return url;
    const baseUrl = SITE_CONFIG.apiUrl;
    const normalizedUrl = url.startsWith('/') ? url : `/${url}`;
    return `${baseUrl}${normalizedUrl}`;
}

/**
 * Renders the user profile section in the sidebar.
 * @param {string} containerId - The ID of the container element.
 */
export function renderSidebarProfile(containerId = "sidebarProfile") {
    const container = document.getElementById(containerId);
    if (!container) return;

    const userName = localStorage.getItem("userName") || "User";
    const role = localStorage.getItem("role") || "USER";

    // Generate initials for avatar
    const initials = userName
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .substring(0, 2);

    // Map roles to display labels
    const roleLabels = {
        'HR': 'HR Manager',
        'PANEL': 'Interviewer',
        'CANDIDATE': 'Candidate'
    };

    container.innerHTML = `
        <div class="avatar-circle">${initials}</div>
        <div class="user-info">
            <span class="user-name" title="${userName}">${userName}</span>
            <span class="role-badge-pill">${roleLabels[role] || role}</span>
        </div>
    `;
}

/**
 * Displays an error message below a specific input field.
 * @param {string} inputId - ID of the input element.
 * @param {string} message - Error message to display.
 */
export function showFieldError(inputId, message) {
    const input = document.getElementById(inputId);
    if (!input) return;

    // Add error class to input
    input.classList.add('input-error');

    // Remove existing error text if any
    let errorEl = input.parentNode.querySelector('.error-text');
    if (!errorEl) {
        errorEl = document.createElement('span');
        errorEl.className = 'error-text';
        input.parentNode.appendChild(errorEl);
    }
    errorEl.textContent = message;
}

/**
 * Clears all field-level errors in a form or container.
 * @param {string} containerId - ID of the form or container.
 */
export function clearErrors(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    // Remove error classes from inputs
    container.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    
    // Remove error text elements
    container.querySelectorAll('.error-text').forEach(el => el.remove());
}

/**
 * Trims whitespace from all text inputs and textareas in a form.
 * @param {string} formId - ID of the form.
 * @returns {Object} A map of trimmed values.
 */
export function getTrimmedValues(formId) {
    const form = document.getElementById(formId);
    if (!form) return {};

    const values = {};
    form.querySelectorAll('input, textarea, select').forEach(input => {
        if (input.id) {
            values[input.id] = input.value.trim();
            // Update the actual input value in the DOM as well
            if (input.tagName !== 'SELECT') {
                input.value = input.value.trim();
            }
        }
    });
    return values;
}

/**
 * Attaches real-time trimming to all text inputs in a form.
 * Ensures that spaces-only inputs are caught by required validation.
 * @param {string} formId - ID of the form to initialize.
 */
export function initFormCleanup(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.querySelectorAll('input, textarea').forEach(input => {
        // Trim on blur (when user leaves the field)
        input.addEventListener('blur', () => {
            input.value = input.value.trim();
        });

        // Optional: prevent typing only spaces at the start
        input.addEventListener('input', () => {
            if (input.value.length > 0 && input.value.trim().length === 0) {
                input.value = "";
            }
        });
    });
}
