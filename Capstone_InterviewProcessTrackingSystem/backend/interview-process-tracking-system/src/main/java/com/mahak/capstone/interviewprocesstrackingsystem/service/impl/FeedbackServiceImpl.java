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
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    private final FeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final PanelProfileRepository panelRepository;
    private final FeedbackMapper mapper;
    private final FeedbackValidation validation;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            InterviewRepository interviewRepository,
            PanelProfileRepository panelRepository,
            FeedbackMapper mapper,
            FeedbackValidation validation) {

        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.panelRepository = panelRepository;
        this.mapper = mapper;
        this.validation = validation;
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

        PanelProfile panel = panelRepository.findById(dto.getPanelId())
                .orElseThrow(() -> {
                    logger.error("Panel not found: {}", dto.getPanelId());
                    return new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND);
                });

        // SRS Rule: One feedback per panel per interview
        boolean exists = feedbackRepository
                .existsByInterviewIdAndPanelId(dto.getInterviewId(), dto.getPanelId());

        if (exists) {
            logger.error("Duplicate feedback attempt for interviewId: {}, panelId: {}", 
                    dto.getInterviewId(), dto.getPanelId());
            throw new InvalidRequestException(ErrorConstants.FEEDBACK_ALREADY_EXISTS);
        }

        Feedback feedback = mapper.toEntity(dto, interview, panel);

        feedback = feedbackRepository.save(feedback);

        logger.info("Feedback submitted successfully with id: {}", feedback.getId());

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


    @Override
    public List<FeedbackDetailResponseDTO> getFeedbackByInterview(Long interviewId) {

        // validate interview exists 
        interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        //fetch feedbacks
        List<Feedback> feedbackList = feedbackRepository.findByInterviewId(interviewId);

        //convert to DTO
        return feedbackList.stream()
                .map(mapper::toDetailDTO)
                .toList();
        }
}