package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;

import jakarta.mail.internet.MimeMessage;

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

    private String buildHtml(String title, String content) {
        return "<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;'>" +
               "  <div style='background: linear-gradient(135deg, #4f46e5, #7c3aed); padding: 30px; text-align: center; color: white;'>" +
               "    <h1 style='margin: 0; font-size: 24px;'>" + title + "</h1>" +
               "  </div>" +
               "  <div style='padding: 30px; color: #1e293b; line-height: 1.6;'>" +
               content +
               "    <p style='margin-top: 30px;'>Best Regards,<br><strong>" + appName + " Team</strong></p>" +
               "  </div>" +
               "  <div style='background: #f8fafc; padding: 20px; text-align: center; color: #64748b; font-size: 12px; border-top: 1px solid #e2e8f0;'>" +
               "    This is an automated message. Please do not reply directly to this email." +
               "  </div>" +
               "</div>";
    }

    @Override
    @Async
    public void sendInterviewScheduleToCandidate(
            String toEmail, String candidateName, String jobTitle,
            String stage, String dateTime, String focusArea) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Interview Invitation: " + stage + " Round");

            String content = "<p>Dear <strong>" + candidateName + "</strong>,</p>" +
                    "<p>We are excited to move forward with your application for the <strong>" + jobTitle + "</strong> position! You have been scheduled for an interview round.</p>" +
                    "<div style='background: #f1f5f9; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                    "  <p style='margin: 5px 0;'><strong>Round:</strong> " + stage + "</p>" +
                    "  <p style='margin: 5px 0;'><strong>Date & Time:</strong> " + dateTime + "</p>" +
                    "  <p style='margin: 5px 0;'><strong>Focus Area:</strong> " + focusArea + "</p>" +
                    "</div>" +
                    "<p>Please ensure you are available at the specified time. We look forward to speaking with you!</p>";

            helper.setText(buildHtml("Interview Scheduled", content), true);
            mailSender.send(mimeMessage);
            logger.info("Interview schedule email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send interview email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPanelAssignmentEmail(
            String toEmail, String panelName, String candidateName, String jobTitle,
            String stage, String dateTime, String focusArea) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | New Interview Assignment");

            String content = "<p>Hello <strong>" + panelName + "</strong>,</p>" +
                    "<p>You have been assigned to conduct an interview for the following candidate:</p>" +
                    "<div style='background: #f1f5f9; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                    "  <p style='margin: 5px 0;'><strong>Candidate:</strong> " + candidateName + "</p>" +
                    "  <p style='margin: 5px 0;'><strong>Position:</strong> " + jobTitle + "</p>" +
                    "  <p style='margin: 5px 0;'><strong>Round:</strong> " + stage + "</p>" +
                    "  <p style='margin: 5px 0;'><strong>Date & Time:</strong> " + dateTime + "</p>" +
                    "  <p style='margin: 5px 0;'><strong>Focus Area:</strong> " + focusArea + "</p>" +
                    "</div>" +
                    "<p>Please log in to your dashboard to view the candidate details and prepare for the evaluation.</p>";

            helper.setText(buildHtml("New Interview Assignment", content), true);
            mailSender.send(mimeMessage);
            logger.info("Panel assignment email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send panel email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPanelAssignedToCandidateEmail(
            String toEmail, String candidateName, String panelName, String stage, String dateTime) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Interviewer Assigned");

            String content = "<p>Dear <strong>" + candidateName + "</strong>,</p>" +
                    "<p>Your interviewer has been assigned for the upcoming <strong>" + stage + "</strong> round.</p>" +
                    "<p style='font-size: 1.1rem; color: #4f46e5;'><strong>Interviewer:</strong> " + panelName + "</p>" +
                    "<p>Please be prepared for the discussion at <strong>" + dateTime + "</strong>.</p>";

            helper.setText(buildHtml("Interviewer Assigned", content), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to notify candidate: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPanelOnboardingEmail(String toEmail, String panelName, String loginUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Your Interviewer Account");

            String content = "<p>Hello <strong>" + panelName + "</strong>,</p>" +
                    "<p>Welcome to the <strong>" + appName + "</strong> panel! An account has been created for you to manage your interview assignments and submit feedback.</p>" +
                    "<p>Please click the button below to set up your password and access your dashboard:</p>" +
                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "  <a href='" + loginUrl + "' style='background: #4f46e5; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;'>Set Up Password</a>" +
                    "</div>" +
                    "<p>If the button doesn't work, copy and paste this link: <br>" + loginUrl + "</p>";

            helper.setText(buildHtml("Account Onboarding", content), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send onboarding email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordSetupEmail(String toEmail, String fullName, String setupUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Complete Your Registration");

            String content = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                    "<p>Welcome to <strong>" + appName + "</strong>! We are excited to have you on board.</p>" +
                    "<p>To complete your registration and secure your account, please set your password by clicking the button below:</p>" +
                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "  <a href='" + setupUrl + "' style='background: #4f46e5; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;'>Set Password</a>" +
                    "</div>";

            helper.setText(buildHtml("Password Setup", content), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send setup email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendSelectionEmail(String toEmail, String candidateName, String jobTitle) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Congratulations! Selection Update");

            String content = "<p>Dear <strong>" + candidateName + "</strong>,</p>" +
                    "<p>We are absolutely thrilled to inform you that you have been <strong>SELECTED</strong> for the <strong>" + jobTitle + "</strong> position at " + appName + "!</p>" +
                    "<p>Your performance throughout the interview rounds was exceptional, and we believe you will be a fantastic addition to our team.</p>" +
                    "<p>Our HR team will reach out to you shortly with the formal offer letter and next steps.</p>" +
                    "<p>Congratulations once again!</p>";

            helper.setText(buildHtml("Selection Update", content), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send selection email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendRejectionEmail(String toEmail, String candidateName, String jobTitle) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Application Update - " + jobTitle);

            String content = "<p>Dear <strong>" + candidateName + "</strong>,</p>" +
                    "<p>Thank you for giving us the opportunity to consider you for the <strong>" + jobTitle + "</strong> position.</p>" +
                    "<p>While we were impressed with your background, we have decided to move forward with other candidates who more closely match our current requirements.</p>" +
                    "<p>We appreciate the time you invested in our recruitment process and wish you the very best in your future endeavors.</p>";

            helper.setText(buildHtml("Application Update", content), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send rejection email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendCancellationEmail(String toEmail, String recipientName, String stage, String dateTime, boolean isPanelist) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Interview Cancellation: " + stage + " Round");

            String intro = isPanelist ? "Hello <strong>" + recipientName + "</strong>," : "Dear <strong>" + recipientName + "</strong>,";
            String reason = isPanelist ? "The interview you were assigned to has been cancelled." : "We regret to inform you that your upcoming interview has been cancelled.";

            String content = "<p>" + intro + "</p>" +
                    "<p>" + reason + "</p>" +
                    "<div style='background: #fee2e2; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #fecaca;'>" +
                    "  <p style='margin: 5px 0; color: #991b1b;'><strong>Cancelled Round:</strong> " + stage + "</p>" +
                    "  <p style='margin: 5px 0; color: #991b1b;'><strong>Original Time:</strong> " + dateTime + "</p>" +
                    "</div>" +
                    "<p>No further action is required from your side at this time. We apologize for any inconvenience caused.</p>";

            helper.setText(buildHtml("Interview Cancelled", content), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Failed to send cancellation email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendRescheduledEmail(String toEmail, String recipientName, String stage, String newDateTime, boolean isPanelist) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " | Interview Rescheduled: " + stage + " Round");

            String intro = isPanelist ? "Hello <strong>" + recipientName + "</strong>," : "Dear <strong>" + recipientName + "</strong>,";
            
            String content = "<p>" + intro + "</p>" +
                    "<p>Your upcoming interview round has been <strong>RESCHEDULED</strong> to a new time.</p>" +
                    "<div style='background: #f0f9ff; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #bae6fd;'>" +
                    "  <p style='margin: 5px 0; color: #0369a1;'><strong>Round:</strong> " + stage + "</p>" +
                    "  <p style='margin: 5px 0; color: #0369a1;'><strong>New Date & Time:</strong> " + newDateTime + "</p>" +
                    "</div>" +
                    "<p>Please update your calendar accordingly. We look forward to your participation!</p>";

            helper.setText(buildHtml("Interview Rescheduled", content), true);
            mailSender.send(mimeMessage);
            logger.info("Rescheduled email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send rescheduling email: {}", e.getMessage());
        }
    }
}