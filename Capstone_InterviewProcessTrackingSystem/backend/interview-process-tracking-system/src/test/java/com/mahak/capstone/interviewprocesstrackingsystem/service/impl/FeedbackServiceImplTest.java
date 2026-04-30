package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackDetailResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.InterviewPanelAssignment;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.FeedbackMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.FeedbackRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewPanelAssignmentRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.FeedbackValidation;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private InterviewRepository interviewRepository;
    @Mock private PanelProfileRepository panelRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private InterviewPanelAssignmentRepository assignmentRepository;
    @Mock private FeedbackMapper mapper;
    @Mock private FeedbackValidation validation;
    @Mock private JdbcTemplate jdbcTemplate;

    @InjectMocks private FeedbackServiceImpl feedbackService;

    private Interview interview;
    private PanelProfile panel;

    @BeforeEach
    void setUp() {
        interview = new Interview();
        ReflectionTestUtils.setField(interview, "id", 1L);
        interview.setInterviewDateTime(LocalDateTime.now().minusHours(1));
        interview.setStage(InterviewStage.L1);
        
        CandidateProfile candidate = new CandidateProfile();
        ReflectionTestUtils.setField(candidate, "id", 1L);
        interview.setCandidate(candidate);

        panel = new PanelProfile();
        ReflectionTestUtils.setField(panel, "id", 10L);
    }

    @Test
    void submitFeedback_Fail_BeforeStart() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        
        interview.setInterviewDateTime(LocalDateTime.now().plusHours(1));
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        
        assertThrows(InvalidRequestException.class, () -> feedbackService.submitFeedback(dto));
    }

    @Test
    void submitFeedback_Fail_HR_OutsideHRStage() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(null); // HR feedback
        
        interview.setStage(InterviewStage.L1);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        
        assertThrows(InvalidRequestException.class, () -> feedbackService.submitFeedback(dto));
    }

    @Test
    void submitFeedback_Fail_PanelNotAssigned() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(10L);
        
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findById(10L)).thenReturn(Optional.of(panel));
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), anyLong(), anyLong())).thenReturn(false);
        
        assertThrows(InvalidRequestException.class, () -> feedbackService.submitFeedback(dto));
    }

    @Test
    void submitFeedback_Success_InterviewCompleted() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setInterviewId(1L);
        dto.setPanelId(10L);
        
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findById(10L)).thenReturn(Optional.of(panel));
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), anyLong(), anyLong())).thenReturn(true);
        when(feedbackRepository.existsByInterviewIdAndPanelId(1L, 10L)).thenReturn(false);
        
        Feedback feedback = new Feedback();
        when(mapper.toEntity(any(), any(), any())).thenReturn(feedback);
        when(feedbackRepository.save(any())).thenReturn(feedback);
        when(mapper.toResponseDTO(any())).thenReturn(new FeedbackResponseDTO());
        
        // Mocking completion check
        when(assignmentRepository.findByInterviewId(1L)).thenReturn(List.of(new InterviewPanelAssignment()));
        when(feedbackRepository.findByInterviewId(1L)).thenReturn(List.of(feedback));
        
        FeedbackResponseDTO response = feedbackService.submitFeedback(dto);
        assertNotNull(response);
        assertEquals(InterviewStatus.COMPLETED, interview.getStatus());
    }

    @Test
    void getFeedbackByInterview_RolePanel_Filter() {
        Feedback f1 = new Feedback();
        f1.setPanel(panel);
        Feedback f2 = new Feedback();
        PanelProfile p2 = new PanelProfile();
        ReflectionTestUtils.setField(p2, "id", 20L);
        f2.setPanel(p2);
        
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(feedbackRepository.findByInterviewId(1L)).thenReturn(List.of(f1, f2));
        
        List<FeedbackDetailResponseDTO> results = feedbackService.getFeedbackByInterview(1L, "ROLE_PANEL", 10L);
        assertEquals(1, results.size());
    }
}
