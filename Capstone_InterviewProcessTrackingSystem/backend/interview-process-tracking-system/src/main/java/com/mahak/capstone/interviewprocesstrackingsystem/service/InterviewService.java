package com.mahak.capstone.interviewprocesstrackingsystem.service;

import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
/**
 * Service interface for managing interview operations.
 */
public interface InterviewService {

    /**
     * Schedule a new interview.
     */
    InterviewResponseDTO scheduleInterview(InterviewRequestDTO dto);

    /**
     * Assign panel to interview.
     */
    void assignPanel(PanelAssignmentRequestDTO dto);

    /**
     * Fetch interview by ID.
     */
    InterviewResponseDTO getInterviewById(Long id);

    /**
     * Fetch all interviews
     */
    List<InterviewResponseDTO> getAllInterviews();
    
    /**
     * get interviews by Candidate.
     */

    List<InterviewResponseDTO> getInterviewsByCandidate(Long candidateId);
}