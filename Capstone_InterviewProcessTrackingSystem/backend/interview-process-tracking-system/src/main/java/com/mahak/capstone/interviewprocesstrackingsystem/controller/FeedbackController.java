package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackDetailResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.FeedbackService;

import jakarta.validation.Valid;

/**
 * REST Controller for Panel Feedback operations.
 * Panel members submit feedback; HR views all feedback.
 */
@RestController
@RequestMapping(ApiConstants.FEEDBACK)
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * PANEL: Submit feedback for an interview.
     * POST /api/feedback
     */
    @PostMapping
    @PreAuthorize("hasRole('PANEL')")
    public ApiResponseDTO<FeedbackResponseDTO> submitFeedback(
            @Valid @RequestBody FeedbackRequestDTO dto) {

        logger.info("Feedback submission received for interviewId: {}", dto.getInterviewId());
        FeedbackResponseDTO response = feedbackService.submitFeedback(dto);
        logger.info("Feedback submitted successfully with id: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_SUBMITTED, response);
    }

    /**
     * HR: Get all feedback for an interview.
     * GET /api/feedback/interview/{interviewId}
     */
    @GetMapping(ApiConstants.BY_INTERVIEW)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<FeedbackDetailResponseDTO>> getFeedbackByInterview(
            @PathVariable Long interviewId) {

        logger.info("Fetching feedback for interviewId: {}", interviewId);
        List<FeedbackDetailResponseDTO> list = feedbackService.getFeedbackByInterview(interviewId);
        logger.info("Fetched {} feedback entries for interviewId: {}", list.size(), interviewId);
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_FETCHED, list);
    }

    /**
     * HR/PANEL: Get feedback by ID.
     * GET /api/feedback/{id}
     */
    @GetMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    public ApiResponseDTO<FeedbackResponseDTO> getFeedbackById(@PathVariable Long id) {

        logger.info("Fetching feedback with id: {}", id);
        FeedbackResponseDTO response = feedbackService.getFeedbackById(id);
        logger.info("Feedback fetched: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_FETCHED, response);
    }
}