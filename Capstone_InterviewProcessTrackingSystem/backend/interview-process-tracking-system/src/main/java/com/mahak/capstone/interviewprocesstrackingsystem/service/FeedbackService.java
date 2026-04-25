package com.mahak.capstone.interviewprocesstrackingsystem.service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;

/**
 * Service interface for handling feedback operations.
 */
public interface FeedbackService {

    /**
     * Submit feedback for an interview by a panel member.
     *
     * @param dto Feedback request data
     * @return FeedbackResponseDTO containing saved feedback details
     */
    FeedbackResponseDTO submitFeedback(FeedbackRequestDTO dto);

    /**
     * Fetch feedback by ID.
     *
     * @param id feedback ID
     * @return FeedbackResponseDTO
     */
    FeedbackResponseDTO getFeedbackById(Long id);
}