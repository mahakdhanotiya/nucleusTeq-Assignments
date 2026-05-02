package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * Submit feedback for an interview.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PANEL','HR')")
    public ApiResponseDTO<FeedbackResponseDTO> submitFeedback(
            @Valid @RequestBody FeedbackRequestDTO dto) {

        logger.info("Feedback submission received for interviewId: {}", dto.getInterviewId());
        FeedbackResponseDTO response = feedbackService.submitFeedback(dto);
        logger.info("Feedback submitted successfully with id: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_SUBMITTED, response);
    }

    /**
     * Get feedback for an interview (HR sees all, Panelists see only their own).
     */
    @GetMapping(ApiConstants.BY_INTERVIEW)
    @PreAuthorize("hasAnyRole('HR','PANEL')")
    public ApiResponseDTO<List<FeedbackDetailResponseDTO>> getFeedbackByInterview(
            @PathVariable Long interviewId,
            @RequestParam(required = false) Long requesterPanelId) {

        logger.info("Fetching feedback for interviewId: {}", interviewId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = "USER";
        if (auth != null && !auth.getAuthorities().isEmpty()) {
            role = auth.getAuthorities().iterator().next().getAuthority();
        }
        
        List<FeedbackDetailResponseDTO> list = feedbackService.getFeedbackByInterview(interviewId, role, requesterPanelId);
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_FETCHED, list);
    }

    /**
     * HR: Get all feedback for a candidate across all interviews.
     */
    @GetMapping(ApiConstants.BY_CANDIDATE)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<FeedbackDetailResponseDTO>> getFeedbackByCandidate(@PathVariable Long candidateId) {
        logger.info("Fetching feedback history for candidateId: {}", candidateId);
        List<FeedbackDetailResponseDTO> list = feedbackService.getFeedbackByCandidate(candidateId);
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_FETCHED, list);
    }

    /**
     * Get feedback by ID.
     */
    @GetMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasAnyRole('HR', 'PANEL')")
    public ApiResponseDTO<FeedbackResponseDTO> getFeedbackById(@PathVariable Long id) {
        logger.info("Fetching feedback with id: {}", id);
        FeedbackResponseDTO response = feedbackService.getFeedbackById(id);
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_FETCHED, response);
    }

    /**
     * HR: Get all feedback in the system.
     */
    @GetMapping(ApiConstants.ALL)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<FeedbackResponseDTO>> getAllFeedback() {
        logger.info("Fetching all feedback entries");
        List<FeedbackResponseDTO> list = feedbackService.getAllFeedback();
        return new ApiResponseDTO<>(true, ApiConstants.FEEDBACK_FETCHED, list);
    }
}