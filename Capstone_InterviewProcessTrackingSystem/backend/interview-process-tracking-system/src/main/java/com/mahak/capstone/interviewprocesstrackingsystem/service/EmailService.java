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

    /** Send panel assignment email with candidate details */
    void sendPanelAssignmentEmail(
        String toEmail,
        String panelName,
        String candidateName,
        String jobTitle,
        String stage,
        String dateTime,
        String focusArea
    );

    /** Send panel onboarding email with login link */
    void sendPanelOnboardingEmail(
        String toEmail,
        String panelName,
        String loginUrl
    );
}