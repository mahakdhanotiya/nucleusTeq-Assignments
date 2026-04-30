package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock private JavaMailSender mailSender;
    @Mock private MimeMessage mimeMessage;
    @InjectMocks private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@hr.com");
        ReflectionTestUtils.setField(emailService, "appName", "InterviewApp");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendInterviewScheduleToCandidate_Success() {
        emailService.sendInterviewScheduleToCandidate("can@test.com", "Can", "Dev", "L1", "Time", "Java");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPanelAssignmentEmail_Success() {
        emailService.sendPanelAssignmentEmail("panel@test.com", "Panel", "Can", "Dev", "L1", "Time", "Java");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPanelAssignedToCandidateEmail_Success() {
        emailService.sendPanelAssignedToCandidateEmail("can@test.com", "Can", "Panel", "L1", "Time");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPanelOnboardingEmail_Success() {
        emailService.sendPanelOnboardingEmail("panel@test.com", "Panel", "url");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordSetupEmail_Success() {
        emailService.sendPasswordSetupEmail("u@t.com", "Name", "url");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_Fail_ExceptionHandled() {
        doThrow(new RuntimeException("Mail error")).when(mailSender).send(any(MimeMessage.class));
        // Should not throw exception, just log it
        emailService.sendPasswordSetupEmail("u@t.com", "Name", "url");
        verify(mailSender).send(any(MimeMessage.class));
    }
}
