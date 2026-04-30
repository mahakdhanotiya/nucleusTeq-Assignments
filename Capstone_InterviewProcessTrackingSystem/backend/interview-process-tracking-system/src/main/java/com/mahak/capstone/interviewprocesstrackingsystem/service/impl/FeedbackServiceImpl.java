package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackDetailResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.FeedbackMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.FeedbackRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.FeedbackService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.FeedbackValidation;

/**
 * Implementation of FeedbackService containing business logic.
 */
@Service
@org.springframework.transaction.annotation.Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    private final FeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final PanelProfileRepository panelRepository;
    private final com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository candidateRepository;
    private final FeedbackMapper mapper;
    private final FeedbackValidation validation;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    private final com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewPanelAssignmentRepository assignmentRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            InterviewRepository interviewRepository,
            PanelProfileRepository panelRepository,
            com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository candidateRepository,
            FeedbackMapper mapper,
            FeedbackValidation validation,
            org.springframework.jdbc.core.JdbcTemplate jdbcTemplate,
            com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewPanelAssignmentRepository assignmentRepository) {

        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.panelRepository = panelRepository;
        this.candidateRepository = candidateRepository;
        this.mapper = mapper;
        this.validation = validation;
        this.jdbcTemplate = jdbcTemplate;
        this.assignmentRepository = assignmentRepository;
    }

    @jakarta.annotation.PostConstruct
    public void initDb() {
        try {
            jdbcTemplate.execute("ALTER TABLE feedbacks ALTER COLUMN panel_id DROP NOT NULL");
            logger.info("Successfully dropped NOT NULL constraint on panel_id in feedbacks table.");
        } catch (Exception e) {
            logger.warn(
                    "Could not alter feedbacks table panel_id constraint. It might already be dropped or table doesn't exist. {}",
                    e.getMessage());
        }
    }

    /**
     * Submit feedback for an interview.
     */
    @Override
    public FeedbackResponseDTO submitFeedback(FeedbackRequestDTO dto) {

        logger.info("Submitting feedback for interviewId: {}, panelId: {}",
                dto.getInterviewId(), dto.getPanelId());

        // Validation
        validation.validateFeedbackRequest(dto);

        Interview interview = interviewRepository.findById(dto.getInterviewId())
                .orElseThrow(() -> {
                    logger.error("Interview not found: {}", dto.getInterviewId());
                    return new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND);
                });

        // SRS Rule: Feedback can only be submitted after the interview has started
        if (interview.getInterviewDateTime() != null &&
                java.time.LocalDateTime.now().isBefore(interview.getInterviewDateTime())) {
            logger.error("Feedback attempt before interview start time. Scheduled: {}",
                    interview.getInterviewDateTime());
            throw new InvalidRequestException(ErrorConstants.FEEDBACK_BEFORE_START);
        }

        PanelProfile panel = null;
        if (dto.getPanelId() != null) {
            panel = panelRepository.findById(dto.getPanelId())
                    .orElseThrow(() -> {
                        logger.error("Panel not found: {}", dto.getPanelId());
                        return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                    });
        }

        // SRS Rule: HR can only give feedback in HR round
        if (dto.getPanelId() == null) {
            if (interview.getStage() != com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.HR) {
                logger.error("HR feedback attempt outside HR stage. Current stage: {}", interview.getStage());
                throw new com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException(
                        ErrorConstants.HR_FEEDBACK_ONLY_IN_HR_STAGE);
            }
        } else {
            // SRS Rule: Panelist must be assigned to this interview
            boolean isAssigned = jdbcTemplate.queryForObject(
                    "SELECT EXISTS(SELECT 1 FROM interview_panel_assignments WHERE interview_id = ? AND panel_id = ?)",
                    Boolean.class, dto.getInterviewId(), dto.getPanelId());
            if (!isAssigned) {
                logger.error("Panelist {} not assigned to interview {}", dto.getPanelId(), dto.getInterviewId());
                throw new InvalidRequestException(ErrorConstants.PANEL_NOT_ASSIGNED_TO_INTERVIEW);
            }
        }

        // SRS Rule: One feedback per person per interview
        boolean exists = feedbackRepository.existsByInterviewIdAndPanelId(dto.getInterviewId(), dto.getPanelId());
        if (exists) {
            logger.error("Duplicate feedback attempt for interviewId: {}, panelId: {}",
                    dto.getInterviewId(), dto.getPanelId());
            throw new InvalidRequestException(ErrorConstants.FEEDBACK_ALREADY_EXISTS);
        }

        Feedback feedback = mapper.toEntity(dto, interview, panel);
        feedback = feedbackRepository.save(feedback);

        // SRS Rule: Check if ALL assigned panelists have submitted feedback
        int assignedCount = assignmentRepository.findByInterviewId(dto.getInterviewId()).size();
        int feedbackCount = feedbackRepository.findByInterviewId(dto.getInterviewId()).size();

        if (feedbackCount >= assignedCount) {
            // All feedbacks received -> Mark Interview COMPLETED and Candidate EVALUATED
            interview.setStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus.COMPLETED);
            interviewRepository.save(interview);

            if (interview.getCandidate() != null) {
                com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile candidate = interview.getCandidate();
                candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.EVALUATED);
                candidateRepository.save(candidate);
                logger.info("Candidate C-{} status updated to EVALUATED (All {} feedbacks received)", candidate.getId(), feedbackCount);
            }
            logger.info("Feedback submission complete. Interview {} marked COMPLETED.", dto.getInterviewId());
        } else {
            // Partially evaluated
            logger.info("Feedback received ({} of {}). Interview remains in progress.", feedbackCount, assignedCount);
        }

        return mapper.toResponseDTO(feedback);
    }

    /**
     * Fetch feedback by ID.
     */
    @Override
    public FeedbackResponseDTO getFeedbackById(Long id) {

        logger.info("Fetching feedback with id: {}", id);

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Feedback not found: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.FEEDBACK_NOT_FOUND);
                });

        return mapper.toResponseDTO(feedback);
    }

    /**
     * Fetch all feedback for a given interview ID.
     */

    /**
     * Fetch all feedback for a given interview ID.
     * HR can see all; Panels can only see their own feedback.
     *
     * @param interviewId the interview ID
     * @param role the requester role
     * @param requesterPanelId the panel ID if requester is a panelist
     * @return list of detailed feedback DTOs
     */
    @Override
    public List<FeedbackDetailResponseDTO> getFeedbackByInterview(Long interviewId, String role,
            Long requesterPanelId) {

        // validate interview exists
        interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));

        // fetch feedbacks
        List<Feedback> feedbackList = feedbackRepository.findByInterviewId(interviewId);

        // Filter: Panels can ONLY see their own feedback
        if ("ROLE_PANEL".equals(role)) {
            feedbackList = feedbackList.stream()
                    .filter(f -> f.getPanel() != null && Objects.equals(f.getPanel().getId(), requesterPanelId))
                    .toList();
        }

        // convert to DTO
        return feedbackList.stream()
                .map(mapper::toDetailDTO)
                .toList();
    }

    /**
     * Get all feedback for a candidate across all interviews.
     *
     * @param candidateId the candidate ID
     * @return list of detailed feedback DTOs
     */
    @Override
    public List<FeedbackDetailResponseDTO> getFeedbackByCandidate(Long candidateId) {
        // Find all interviews for this candidate
        List<Interview> interviews = interviewRepository.findByCandidateId(candidateId);

        // Collect all feedback from those interviews
        List<Feedback> allFeedback = new java.util.ArrayList<>();
        for (Interview interview : interviews) {
            allFeedback.addAll(feedbackRepository.findByInterviewId(interview.getId()));
        }

        return allFeedback.stream()
                .map(mapper::toDetailDTO)
                .toList();
    }

    /**
     * Get all feedback submitted in the system.
     *
     * @return list of feedback DTOs
     */
    @Override
    public List<FeedbackResponseDTO> getAllFeedback() {
        logger.info("Fetching all feedback in the system");
        List<Feedback> allFeedback = feedbackRepository.findAll();
        return allFeedback.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
}