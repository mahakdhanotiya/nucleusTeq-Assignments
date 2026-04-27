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

import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.CandidateService;

import jakarta.validation.Valid;

/**
 * Controller handles HTTP requests related to Candidate
 */
@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;

    // constructor injection
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /**
     * Create Candidate (Apply for Job)
     * POST /candidates
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> createCandidate(
            @Valid @RequestBody CandidateRequestDTO requestDTO) {

        logger.info("Received request to create candidate for userId: {}", requestDTO.getUserId());

        CandidateResponseDTO response = candidateService.createCandidate(requestDTO);

        logger.info("Candidate created successfully with id: {}", response.getId());

        return new ApiResponseDTO<>(true, "Candidate created successfully", response);
    }

    /**
     * Get All Candidates
     * GET /candidates
     */
    @GetMapping
    @PreAuthorize("hasRole('HR')")

    public ApiResponseDTO<List<CandidateResponseDTO>> getAllCandidates() {

        logger.info("Fetching all candidates");

        List<CandidateResponseDTO> list = candidateService.getAllCandidates();

        return new ApiResponseDTO<>(true, "Candidates fetched successfully", list);
    }


    /**
     * 
     * candidate get their own profile
     * GET /candidates/my-profile
     */
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> getMyProfile() {
        
       CandidateResponseDTO response = candidateService.getMyProfile();

    return new ApiResponseDTO<>(true, "Profile fetched successfully", response); 
    }


    /**
     * Get Candidate by ID
     * GET /candidates/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR','CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> getCandidateById(@PathVariable Long id) {

        logger.info("Fetching candidate with id: {}", id);

        CandidateResponseDTO response = candidateService.getCandidateById(id);

        return new ApiResponseDTO<>(true, "Candidate fetched successfully", response);
    }

}