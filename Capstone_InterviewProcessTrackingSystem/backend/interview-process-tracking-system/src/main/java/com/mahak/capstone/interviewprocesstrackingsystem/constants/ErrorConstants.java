package com.mahak.capstone.interviewprocesstrackingsystem.constants;

/**
 * Centralized error/validation constants used across services and controllers.
 */
public final class ErrorConstants {

    private ErrorConstants() {} // prevent instantiation

    // AUTH 
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String HR_EMAIL_REQUIRED = "Only emails with '.hr@' pattern can register as HR";

    // JOB 
    public static final String JOB_NOT_FOUND = "Job not found";
    public static final String JOB_ALREADY_INACTIVE = "Job is already inactive";
    public static final String INVALID_EXPERIENCE_RANGE = "Minimum experience cannot be greater than maximum experience";
    public static final String INVALID_SALARY_RANGE = "Minimum salary cannot be greater than maximum salary";

    // CANDIDATE
    public static final String CANDIDATE_NOT_FOUND = "Candidate not found";
    public static final String ACTIVE_APPLICATION_EXISTS = "User already has an active application";
    public static final String INVALID_RESUME_FORMAT = "Only PDF resumes are allowed";
    public static final String INVALID_REQUEST = "Request body cannot be null";

    // INTERVIEW 
    public static final String JD_NOT_FOUND = "Job description not found";
    public static final String INTERVIEW_NOT_FOUND = "Interview not found";
    public static final String INVALID_INTERVIEW_DATE = "Interview date is required";
    public static final String INVALID_STAGE = "Stage is required";
    public static final String INVALID_FOCUS_AREA = "Focus area is required";

    // PANEL
    public static final String PANEL_NOT_FOUND = "Panel not found";
    public static final String PANEL_ALREADY_EXISTS = "Panel profile already exists for this user";
    public static final String PANEL_LIMIT_EXCEEDED = "Maximum 2 panels allowed per interview";
    public static final String INVALID_INTERVIEW_ID = "Interview ID is required";
    public static final String INVALID_PANEL_ID = "Panel ID is required";
    public static final String PANEL_ALREADY_ASSIGNED = "Panel already assigned to this interview";
    public static final String USER_ID_REQUIRED = "User ID is required";
    public static final String ORGANIZATION_REQUIRED = "Organization is required";
    public static final String DESIGNATION_REQUIRED = "Designation is required";
    public static final String MOBILE_REQUIRED = "Mobile number is required";

    // FEEDBACK 
    public static final String INVALID_RATING = "Rating must be between 1 and 5";
    public static final String INVALID_COMMENTS = "Comments are required";
    public static final String FEEDBACK_ALREADY_EXISTS = "Feedback already submitted";
    public static final String FEEDBACK_NOT_FOUND = "Feedback not found";

    // FILE 
    public static final String INVALID_FILE_TYPE = "Only PDF files are allowed";
}