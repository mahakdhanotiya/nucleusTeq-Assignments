package com.mahak.capstone.interviewprocesstrackingsystem.constants;

/**
 * Centralized API constants for endpoints, success messages, and log messages.
 * All hardcoded strings across controllers should reference this class.
 */
public final class ApiConstants {

    private ApiConstants() {} // prevent instantiation

    // BASE PATHS 
    public static final String AUTH = "/auth";
    public static final String CANDIDATES = "/candidates";
    public static final String JOBS = "/jobs";
    public static final String INTERVIEWS = "/api/interviews";
    public static final String PANELS = "/api/panels";
    public static final String FEEDBACK = "/api/feedback";
    public static final String FILES = "/api/files";

    //AUTH ENDPOINTS 
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String SET_PASSWORD = "/set-password";
    public static final String ME = "/me";

    // SUB-PATHS 
    public static final String MY_PROFILE = "/my-profile";
    public static final String UPDATE = "/update";
    public static final String SEARCH = "/search";
    public static final String DEACTIVATE = "/{id}/deactivate";
    public static final String ACTIVATE = "/{id}/activate";
    public static final String ALL = "/all";
    public static final String ASSIGN_PANEL = "/assign-panel";
    public static final String STAGE_PROGRESSION = "/stage-progression";
    public static final String UPLOAD = "/upload";
    public static final String BY_INTERVIEW = "/interview/{interviewId}";
    public static final String BY_CANDIDATE = "/candidate/{candidateId}";
    public static final String BY_ID = "/{id}";
    public static final String STATUS = "/{id}/status";

    // SUCCESS MESSAGES 
    public static final String REGISTER_SUCCESS = "User registered successfully";
    public static final String REGISTRATION_SUCCESS_EMAIL = "Registration successful! Please check your email to set your password.";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String PASSWORD_SET_SUCCESS = "Password set successfully! You can now login.";

    public static final String CANDIDATE_CREATED = "Candidate created successfully";
    public static final String CANDIDATE_FETCHED = "Candidate fetched successfully";
    public static final String CANDIDATES_FETCHED = "Candidates fetched successfully";
    public static final String CANDIDATE_DELETED = "Candidate deleted successfully";
    public static final String PROFILE_FETCHED = "Profile fetched successfully";
    public static final String PROFILE_UPDATED = "Profile updated successfully";

    public static final String JOB_CREATED = "Job created successfully";
    public static final String JOBS_FETCHED = "Jobs fetched successfully";
    public static final String JOB_DEACTIVATED = "Job deactivated successfully";
    public static final String JOB_ACTIVATED = "Job activated successfully";
    public static final String JOB_UPDATED = "Job updated successfully";

    public static final String INTERVIEW_SCHEDULED = "Interview scheduled successfully";
    public static final String INTERVIEW_FETCHED = "Interview fetched successfully";
    public static final String INTERVIEWS_FETCHED = "Interviews fetched successfully";
    public static final String PANEL_ASSIGNED = "Panel assigned successfully";
    public static final String STAGE_UPDATED = "Stage updated successfully";
    public static final String INTERVIEW_STATUS_UPDATED = "Interview status updated successfully";
    public static final String INTERVIEW_UPDATED = "Interview updated successfully";
    public static final String INTERVIEW_DELETED = "Interview deleted successfully";

    public static final String PANEL_CREATED = "Panel created successfully";
    public static final String PANEL_FETCHED = "Panel fetched successfully";
    public static final String PANELS_FETCHED = "Panels fetched successfully";
    public static final String PANEL_UPDATED = "Panel updated successfully";
    public static final String PANEL_DELETED = "Panel deleted successfully";

    public static final String FEEDBACK_SUBMITTED = "Feedback submitted successfully";
    public static final String FEEDBACK_FETCHED = "Feedback fetched successfully";

    public static final String FILE_UPLOADED = "File uploaded successfully";

    // LOG MESSAGES 
    public static final String LOG_REQUEST_RECEIVED = "Request received: {} {}";
    public static final String LOG_RESPONSE_SUCCESS = "Response success: {}";
    public static final String LOG_PROCESSING = "Processing: {}";
}
