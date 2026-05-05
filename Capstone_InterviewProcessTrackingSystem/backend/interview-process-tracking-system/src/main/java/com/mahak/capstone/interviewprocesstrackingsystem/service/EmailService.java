package com.mahak.capstone.interviewprocesstrackingsystem.service;

public interface EmailService {

    /** Send interview schedule email to candidate */
    void sendInterviewScheduleToCandidate(
        String toEmail,
        String candidateName,
        String jobTitle,
        String stage,
        String dateTime,
        String focusArea
    );

    void sendPanelAssignmentEmail(
        String toEmail,
        String panelName,
        String candidateName,
        String jobTitle,
        String stage,
        String dateTime,
        String focusArea
    );

    /** Notify candidate that a panel has been assigned to their round */
    void sendPanelAssignedToCandidateEmail(
        String toEmail,
        String candidateName,
        String panelName,
        String stage,
        String dateTime
    );

    /** Send panel onboarding email with login link */
    void sendPanelOnboardingEmail(
        String toEmail,
        String panelName,
        String loginUrl
    );

    /** Send password setup link email after registration */
    void sendPasswordSetupEmail(String toEmail, String fullName, String setupUrl);

    /** Notify candidate of final selection */
    void sendSelectionEmail(String toEmail, String candidateName, String jobTitle);

    /** Notify candidate of rejection */
    void sendRejectionEmail(String toEmail, String candidateName, String jobTitle);

    /** Notify candidate and panelist of interview cancellation */
    void sendCancellationEmail(String toEmail, String recipientName, String stage, String dateTime, boolean isPanelist);

    /** Notify candidate and panelist of interview rescheduling */
    void sendRescheduledEmail(String toEmail, String recipientName, String stage, String newDateTime, boolean isPanelist);
}