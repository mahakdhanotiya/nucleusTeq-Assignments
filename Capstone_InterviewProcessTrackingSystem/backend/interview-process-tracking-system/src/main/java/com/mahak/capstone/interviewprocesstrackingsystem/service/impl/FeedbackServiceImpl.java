package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.util.List;

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
    private final FeedbackMapper mapper;
    private final FeedbackValidation validation;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            InterviewRepository interviewRepository,
            PanelProfileRepository panelRepository,
            FeedbackMapper mapper,
            FeedbackValidation validation,
            org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {

        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.panelRepository = panelRepository;
        this.mapper = mapper;
        this.validation = validation;
        this.jdbcTemplate = jdbcTemplate;
    }

    @jakarta.annotation.PostConstruct
    public void initDb() {
        try {
            jdbcTemplate.execute("ALTER TABLE feedbacks ALTER COLUMN panel_id DROP NOT NULL");
            logger.info("Successfully dropped NOT NULL constraint on panel_id in feedbacks table.");
        } catch (Exception e) {
            logger.warn("Could not alter feedbacks table panel_id constraint. It might already be dropped or table doesn't exist. {}", e.getMessage());
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

        PanelProfile panel = null;
        if (dto.getPanelId() != null) {
            panel = panelRepository.findById(dto.getPanelId())
                    .orElseThrow(() -> {
                        logger.error("Panel not found: {}", dto.getPanelId());
                        return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                    });
        }

        // SRS Rule: One feedback per panel per interview
        if (dto.getPanelId() != null) {
            boolean exists = feedbackRepository
                    .existsByInterviewIdAndPanelId(dto.getInterviewId(), dto.getPanelId());

            if (exists) {
                logger.error("Duplicate feedback attempt for interviewId: {}, panelId: {}", 
                        dto.getInterviewId(), dto.getPanelId());
                throw new InvalidRequestException(ErrorConstants.FEEDBACK_ALREADY_EXISTS);
            }
        }

        Feedback feedback = mapper.toEntity(dto, interview, panel);
        feedback = feedbackRepository.save(feedback);

        // SRS Rule: Automatically mark interview as COMPLETED after feedback
        interview.setStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus.COMPLETED);
        interviewRepository.save(interview);

        logger.info("Feedback submitted successfully with id: {}, Interview marked COMPLETED", feedback.getId());
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

    @Override
    public List<FeedbackDetailResponseDTO> getFeedbackByInterview(Long interviewId) {

        // validate interview exists 
        interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.INTERVIEW_NOT_FOUND));

        //fetch feedbacks
        List<Feedback> feedbackList = feedbackRepository.findByInterviewId(interviewId);

        //convert to DTO
        return feedbackList.stream()
                .map(mapper::toDetailDTO)
                .toList();
        }
}