package com.mahak.capstone.interviewprocesstrackingsystem.service;
import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;

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

    CandidateResponseDTO getMyProfile();
}

