package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.StageProgressionRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewUpdateDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.InterviewPanelAssignment;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.InterviewMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.CandidateMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.InterviewPanelAssignmentMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.FeedbackRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewPanelAssignmentRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.InterviewValidation;

@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock private InterviewRepository interviewRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private JobDescriptionRepository jdRepository;
    @Mock private PanelProfileRepository panelRepository;
    @Mock private InterviewPanelAssignmentRepository assignmentRepository;
    @Mock private FeedbackRepository feedbackRepository;
    @Mock private InterviewMapper interviewMapper;
    @Mock private InterviewPanelAssignmentMapper assignmentMapper;
    @Mock private InterviewValidation interviewValidation;
    @Mock private EmailService emailService;
    @Mock private UserRepository userRepository;


    @InjectMocks private InterviewServiceImpl interviewService;

    private CandidateProfile candidate;
    private Interview interview;
    private JobDescription jd;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        jd = new JobDescription();
        ReflectionTestUtils.setField(jd, "id", 1L);
        jd.setTitle("Software Engineer");

        candidate = new CandidateProfile();
        ReflectionTestUtils.setField(candidate, "id", 1L);
        candidate.setUser(user);
        candidate.setJobDescription(jd);
        candidate.setCurrentStage(InterviewStage.SCREENING);
        candidate.setApplicationStatus(ApplicationStatus.PROFILING_COMPLETED);

        interview = new Interview();
        ReflectionTestUtils.setField(interview, "id", 1L);
        interview.setCandidate(candidate);
        interview.setJobDescription(jd);
        interview.setStage(InterviewStage.L1);
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        interview.setFocusArea("Test Area");
    }

    @Test
    void scheduleInterview_Success() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setCandidateId(1L);
        dto.setJobDescriptionId(1L);
        dto.setStage("L1");
        dto.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        dto.setFocusArea("Java");

        Interview screening = new Interview();
        screening.setStage(InterviewStage.SCREENING);
        screening.setStatus(InterviewStatus.COMPLETED);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndStage(1L, InterviewStage.SCREENING)).thenReturn(List.of(screening));
        when(jdRepository.findById(1L)).thenReturn(Optional.of(jd));
        when(interviewMapper.toEntity(any(), any(), any())).thenReturn(interview);
        when(interviewRepository.save(any())).thenReturn(interview);
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());
        when(feedbackRepository.findByInterviewId(any())).thenReturn(new ArrayList<>());
        when(assignmentRepository.findByInterviewId(any())).thenReturn(new ArrayList<>());
        when(interviewRepository.findById(any())).thenReturn(Optional.of(interview));

        assertNotNull(interviewService.scheduleInterview(dto));
    }

    @Test
    void progressCandidateStage_SelectionSuccess() {
        StageProgressionRequestDTO dto = new StageProgressionRequestDTO();
        dto.setCandidateId(1L);
        dto.setNewStage("SELECTED");

        candidate.setCurrentStage(InterviewStage.HR);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any())).thenReturn(candidate);

        CandidateResponseDTO response = interviewService.progressCandidateStage(dto);
        assertNotNull(response);
    }

    @Test
    void progressCandidateStage_RejectionSuccess() {
        StageProgressionRequestDTO dto = new StageProgressionRequestDTO();
        dto.setCandidateId(1L);
        dto.setNewStage("REJECTED");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any())).thenReturn(candidate);

        CandidateResponseDTO response = interviewService.progressCandidateStage(dto);
        assertNotNull(response);
    }

    @Test
    void progressCandidateStage_InvalidFlow_Exception() {
        StageProgressionRequestDTO dto = new StageProgressionRequestDTO();
        dto.setCandidateId(1L);
        dto.setNewStage("HR"); // Screenings to HR directly is invalid

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        assertThrows(InvalidRequestException.class, () -> interviewService.progressCandidateStage(dto));
    }

    @Test
    void getAllInterviews_HR_Success() {
        when(interviewRepository.findAll()).thenReturn(List.of(interview));
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());

        List<InterviewResponseDTO> results = interviewService.getAllInterviews("ROLE_HR", "hr@test.com");
        assertFalse(results.isEmpty());
    }

    @Test
    void getAllInterviews_Empty_ReturnsEmptyList() {
        when(interviewRepository.findAll()).thenReturn(new ArrayList<>());
        List<InterviewResponseDTO> results = interviewService.getAllInterviews("ROLE_HR", "hr@test.com");
        assertTrue(results.isEmpty());
    }


    @Test
    void deleteInterview_WithAssignments_Success() {
        interview.setStatus(InterviewStatus.SCHEDULED);
        candidate.setApplicationStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(List.of(new InterviewPanelAssignment()));
        when(candidateRepository.save(any())).thenReturn(candidate);
        
        panelService_deleteInterview(1L);
        
        verify(assignmentRepository).deleteAll(any());
        verify(candidateRepository).save(any());
        verify(interviewRepository).delete(interview);
    }

    @Test
    void deleteInterview_Completed_Exception() {
        interview.setStatus(InterviewStatus.COMPLETED);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        
        assertThrows(InvalidRequestException.class, () -> interviewService.deleteInterview(1L));
    }

    private void panelService_deleteInterview(Long id) {
        interviewService.deleteInterview(id);
    }

    @Test
    void updateInterviewStatus_Success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        interviewService.updateInterviewStatus(1L, InterviewStatus.COMPLETED);
        verify(interviewRepository).save(interview);
    }

    @Test
    void updateInterview_DateChange_SendsEmail() {
        InterviewUpdateDTO dto = new InterviewUpdateDTO();
        LocalDateTime newDate = LocalDateTime.now().plusDays(5);
        dto.setInterviewDateTime(newDate);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any())).thenReturn(interview);
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(new ArrayList<>());

        interviewService.updateInterview(1L, dto);

        verify(emailService).sendRescheduledEmail(any(), any(), any(), any(), eq(false));
    }

    @Test
    void getInterviewById_Success_Enriched() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());
        
        InterviewResponseDTO result = interviewService.getInterviewById(1L);
        
        assertNotNull(result);
        verify(interviewRepository, atLeastOnce()).findById(1L);
    }
    @Test
    void assignPanel_Duplicate_Exception() {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(10L);

        PanelProfile panel = new PanelProfile();
        ReflectionTestUtils.setField(panel, "id", 10L);

        InterviewPanelAssignment assignment = new InterviewPanelAssignment();
        assignment.setPanel(panel);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findById(10L)).thenReturn(Optional.of(panel));
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(List.of(assignment));

        assertThrows(InvalidRequestException.class, () -> interviewService.assignPanel(dto));
    }

    @Test
    void assignPanel_LimitExceeded_Exception() {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(30L);

        PanelProfile p1 = new PanelProfile();
        ReflectionTestUtils.setField(p1, "id", 10L);
        PanelProfile p2 = new PanelProfile();
        ReflectionTestUtils.setField(p2, "id", 20L);

        InterviewPanelAssignment a1 = new InterviewPanelAssignment();
        a1.setPanel(p1);
        InterviewPanelAssignment a2 = new InterviewPanelAssignment();
        a2.setPanel(p2);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findById(30L)).thenReturn(Optional.of(new PanelProfile()));
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(List.of(a1, a2));

        assertThrows(InvalidRequestException.class, () -> interviewService.assignPanel(dto));
    }

    @Test
    void enrichInterviewDTO_NullJob_Success() {
        interview.setJobDescription(null);
        candidate.setJobDescription(null);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());
        
        InterviewResponseDTO result = interviewService.getInterviewById(1L);
        assertEquals("N/A", result.getJobTitle());
    }

    @Test
    void scheduleInterview_PreviousStageNotCompleted_Exception() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setCandidateId(1L);
        dto.setStage("L1");

        // Current stage is SCREENING, but no COMPLETED screening interview found
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndStage(1L, InterviewStage.SCREENING)).thenReturn(new ArrayList<>());

        assertThrows(InvalidRequestException.class, () -> interviewService.scheduleInterview(dto));
    }

    @Test
    void scheduleInterview_JobMismatch_Exception() {
        InterviewRequestDTO dto = new InterviewRequestDTO();
        dto.setCandidateId(1L);
        dto.setJobDescriptionId(2L); // Different JD
        dto.setStage("L1");

        JobDescription otherJd = new JobDescription();
        ReflectionTestUtils.setField(otherJd, "id", 2L);
        otherJd.setTitle("QA Engineer");

        candidate.setCurrentStage(InterviewStage.L1); // Match the stage to pass validation
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(jdRepository.findById(2L)).thenReturn(Optional.of(otherJd));

        assertThrows(InvalidRequestException.class, () -> interviewService.scheduleInterview(dto));
    }

    @Test
    void progressCandidateStage_StageNotCompleted_Exception() {
        StageProgressionRequestDTO dto = new StageProgressionRequestDTO();
        dto.setCandidateId(1L);
        dto.setNewStage("L2");

        candidate.setCurrentStage(InterviewStage.L1);
        // No COMPLETED L1 interview
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        lenient().when(interviewRepository.findByCandidateIdAndStage(1L, InterviewStage.L1)).thenReturn(new ArrayList<>());

        assertThrows(InvalidRequestException.class, () -> interviewService.progressCandidateStage(dto));
    }

    @Test
    void updateInterviewStatus_Cancelled_SendsEmails() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(new ArrayList<>());

        interviewService.updateInterviewStatus(1L, InterviewStatus.CANCELLED);

        verify(emailService, atLeastOnce()).sendCancellationEmail(any(), any(), any(), any(), eq(false));
    }

    @Test
    void getAllInterviews_PanelRole_Success() {
        PanelProfile panel = new PanelProfile();
        ReflectionTestUtils.setField(panel, "id", 10L);
        User panelUser = new User();
        panelUser.setEmail("panel@test.com");
        panel.setUser(panelUser);

        InterviewPanelAssignment assignment = new InterviewPanelAssignment();
        assignment.setPanel(panel);

        when(interviewRepository.findAll()).thenReturn(List.of(interview));
        when(panelRepository.findByUserEmail("panel@test.com")).thenReturn(Optional.of(panel));
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(List.of(assignment));
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());

        List<InterviewResponseDTO> results = interviewService.getAllInterviews("ROLE_PANEL", "panel@test.com");
        assertFalse(results.isEmpty());
    }

    @Test
    void getInterviewsByCandidate_Success() {
        when(interviewRepository.findByCandidateId(1L)).thenReturn(List.of(interview));
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());

        List<InterviewResponseDTO> results = interviewService.getInterviewsByCandidate(1L);
        assertFalse(results.isEmpty());
    }

    @Test
    void getInterviewsByCandidate_Empty_Exception() {
        when(interviewRepository.findByCandidateId(1L)).thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> interviewService.getInterviewsByCandidate(1L));
    }
}
