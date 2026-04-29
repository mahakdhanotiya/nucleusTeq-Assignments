package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends interview schedule details to the candidate.
     * Includes date, time, stage and focus area.
     */
    @Override
    public void sendInterviewScheduleToCandidate(
            String toEmail, String candidateName, String jobTitle,
            String stage, String dateTime, String focusArea) {

        logger.info("Sending interview schedule email to candidate: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Interview Scheduled: " + stage + " Round");
            message.setText(
                "Dear " + candidateName + ",\n\n" +
                "Your interview has been scheduled. Here are the details:\n\n" +
                "Position: " + jobTitle + "\n" +
                "Round: " + stage + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Focus Areas: " + focusArea + "\n\n" +
                "Please be available at the scheduled time.\n\n" +
                "Best regards,\n" + appName + " Team"
            );
            mailSender.send(message);
            logger.info("Interview schedule email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            // Don't throw — email failure should not break interview scheduling
        }
    }

    /**
     * Sends interview assignment details to the panel member.
     * Contains candidate info, role, stage and schedule.
     */
    @Override
    public void sendPanelAssignmentEmail(
            String toEmail, String panelName, String candidateName, String jobTitle,
            String stage, String dateTime, String focusArea) {

        logger.info("Sending panel assignment email to: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - You Have Been Assigned to Interview");
            message.setText(
                "Dear " + panelName + ",\n\n" +
                "You have been assigned to conduct an interview. Details:\n\n" +
                "Candidate: " + candidateName + "\n" +
                "Position: " + jobTitle + "\n" +
                "Round: " + stage + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Your Focus Area: " + focusArea + "\n\n" +
                "Please review candidate profile before the interview.\n\n" +
                "Best regards,\n" + appName + " Team"
            );
            mailSender.send(message);
            logger.info("Panel assignment email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send panel assignment email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Sends onboarding email to newly created panel.
     * Includes login details and access information.
     */
    @Override
    public void sendPanelOnboardingEmail(String toEmail, String panelName, String loginUrl) {

        logger.info("Sending panel onboarding email to: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - You Have Been Added as an Interviewer");
            message.setText(
                "Dear " + panelName + ",\n\n" +
                "You have been added as a panel interviewer in " + appName + ".\n\n" +
                "Please set your password and log in using the link below:\n" +
                loginUrl + "\n\n" +
                "Your registered email: " + toEmail + "\n\n" +
                "Best regards,\n" + appName + " Team"
            );
            mailSender.send(message);
            logger.info("Panel onboarding email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send onboarding email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Sends a password setup link to a newly registered user.
     * The link contains a unique token that allows them to set their password.
     */
    @Override
    public void sendPasswordSetupEmail(String toEmail, String fullName, String setupUrl) {

        logger.info("Sending password setup email to: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Set Your Password");
            message.setText(
                "Dear " + fullName + ",\n\n" +
                "Welcome to " + appName + "! Your account has been created successfully.\n\n" +
                "Please click the link below to set your password:\n" +
                setupUrl + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't create this account, please ignore this email.\n\n" +
                "Best regards,\n" + appName + " Team"
            );
            mailSender.send(message);
            logger.info("Password setup email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send password setup email to {}: {}", toEmail, e.getMessage());
        }
    }
}