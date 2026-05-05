package com.mahak.capstone.interviewprocesstrackingsystem.service;
import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackDetailResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;

/**
 * Service interface for handling feedback operations.
 */
public interface FeedbackService {

    /**
     * Submit feedback for an interview.
     *
     * @param dto Feedback request data
     * @return FeedbackResponseDTO
     */
    FeedbackResponseDTO submitFeedback(FeedbackRequestDTO dto);

    /**
     * Fetch feedback by ID.
     *
     * @param id feedback ID
     * @return FeedbackResponseDTO
     */
    FeedbackResponseDTO getFeedbackById(Long id);

    /**
     * Fetch feedback for an interview with role-based visibility.
     *
     * @param interviewId interview ID
     * @param role user role
     * @param requesterPanelId ID of the panelist making the request (null for HR)
     * @return list of feedback details
     */
    List<FeedbackDetailResponseDTO> getFeedbackByInterview(Long interviewId, String role, Long requesterPanelId);
    
    /**
     * Fetch all feedback for a candidate across all rounds.
     *
     * @param candidateId candidate ID
     * @return list of feedback details
     */
    List<FeedbackDetailResponseDTO> getFeedbackByCandidate(Long candidateId);

    /**
     * Fetch all feedback in the system (HR only).
     *
     * @return list of feedback summaries
     */
    List<FeedbackResponseDTO> getAllFeedback();
}