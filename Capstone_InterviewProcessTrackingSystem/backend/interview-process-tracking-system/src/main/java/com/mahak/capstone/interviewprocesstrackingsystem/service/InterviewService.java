package com.mahak.capstone.interviewprocesstrackingsystem.service;

import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.StageProgressionRequestDTO;
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
    List<InterviewResponseDTO> getAllInterviews(String role, String username);
    
    /**
     * get interviews by Candidate.
     */

    List<InterviewResponseDTO> getInterviewsByCandidate(Long candidateId);

    /**
     * Progress candidate to next stage after interview.
     */
    CandidateResponseDTO progressCandidateStage(StageProgressionRequestDTO dto);

    /**
     * Delete interview by ID.
     */
    void deleteInterview(Long id);

    /**
     * Update interview details.
     */
    InterviewResponseDTO updateInterview(Long id, com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewUpdateDTO dto);

    /**
     * Update interview status (HR only).
     */
    void updateInterviewStatus(Long id, com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus status);
}