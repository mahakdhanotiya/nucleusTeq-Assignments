package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void getAllInterviews_Empty_Exception() {
        when(interviewRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> interviewService.getAllInterviews());
    }

    @Test
    void deleteInterview_WithAssignments_Success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(List.of(new InterviewPanelAssignment()));
        
        panelService_deleteInterview(1L);
        
        verify(assignmentRepository).deleteAll(any());
        verify(interviewRepository).delete(interview);
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
    void updateInterview_PartialFields_Success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any())).thenReturn(interview);
        when(interviewMapper.toResponseDTO(any())).thenReturn(new InterviewResponseDTO());
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview)); // for enrichment
        
        InterviewUpdateDTO dto = new InterviewUpdateDTO();
        dto.setFocusArea("New Focus");
        
        interviewService.updateInterview(1L, dto);
        
        assertEquals("New Focus", interview.getFocusArea());
    }
}
