package com.mahak.capstone.interviewprocesstrackingsystem.service;
import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

public interface CandidateService {

    /**
     * Create new candidate application
     */
    CandidateResponseDTO createCandidate(CandidateRequestDTO candidate);

    /**
     * Get all candidates
     */
    List<CandidateResponseDTO> getAllCandidates();

    /**
     * Get candidate by ID
     */
    CandidateResponseDTO getCandidateById(Long id);

    /**
     * Get current user's candidate profile
     */
    CandidateResponseDTO getMyProfile();

    /**
     * Search/filter candidates by JD, stage, and/or status.
     * All parameters are optional.
     */
    List<CandidateResponseDTO> searchCandidates(Long jdId, InterviewStage stage, ApplicationStatus status, String name);

    /**
     * Candidate updates their own profile
     */
    CandidateResponseDTO updateMyProfile(CandidateRequestDTO dto);

    /**
     * HR: Delete a candidate application
     */
    void deleteCandidate(Long id);
}
