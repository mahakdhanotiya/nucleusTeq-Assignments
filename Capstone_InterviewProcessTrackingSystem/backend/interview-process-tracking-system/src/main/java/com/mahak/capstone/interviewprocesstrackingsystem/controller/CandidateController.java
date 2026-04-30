package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.service.CandidateService;

import jakarta.validation.Valid;

/**
 * Controller handles HTTP requests related to Candidate.
 */
@RestController
@RequestMapping(ApiConstants.CANDIDATES)
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /**
     * Create Candidate (Apply for Job).
     * POST /candidates
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> createCandidate(
            @Valid @RequestBody CandidateRequestDTO requestDTO) {

        logger.info("Create candidate request received for userId: {}", requestDTO.getUserId());
        CandidateResponseDTO response = candidateService.createCandidate(requestDTO);
        logger.info("Candidate created successfully with id: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.CANDIDATE_CREATED, response);
    }

    /**
     * Get All Candidates.
     * GET /candidates
     */
    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<CandidateResponseDTO>> getAllCandidates() {

        logger.info("Fetching all candidates");
        List<CandidateResponseDTO> list = candidateService.getAllCandidates();
        logger.info("Fetched {} candidates", list.size());
        return new ApiResponseDTO<>(true, ApiConstants.CANDIDATES_FETCHED, list);
    }

    /**
     * Candidate gets their own profile.
     * GET /candidates/my-profile
     */
    @GetMapping(ApiConstants.MY_PROFILE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> getMyProfile() {

        logger.info("Fetching logged-in candidate profile");
        CandidateResponseDTO response = candidateService.getMyProfile();
        logger.info("Profile fetched for candidateId: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.PROFILE_FETCHED, response);
    }

    /**
     * Candidate updates their own profile.
     * PUT /candidates/update
     */
    @PutMapping(ApiConstants.UPDATE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> updateMyProfile(
            @RequestBody CandidateRequestDTO requestDTO) {

        logger.info("Candidate updating profile");
        CandidateResponseDTO response = candidateService.updateMyProfile(requestDTO);
        logger.info("Profile updated for candidateId: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.PROFILE_UPDATED, response);
    }

    /**
     * Get Candidate by ID.
     * GET /candidates/{id}
     */
    @GetMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasAnyRole('HR','CANDIDATE')")
    public ApiResponseDTO<CandidateResponseDTO> getCandidateById(@PathVariable Long id) {

        logger.info("Fetching candidate with id: {}", id);
        CandidateResponseDTO response = candidateService.getCandidateById(id);
        logger.info("Candidate fetched: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.CANDIDATE_FETCHED, response);
    }

    /**
     * HR: Search/filter candidates by JD, stage, and/or status.
     * GET /candidates/search?jdId=1&stage=L1&status=SELECTED
     */
    @GetMapping(ApiConstants.SEARCH)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<CandidateResponseDTO>> searchCandidates(
            @RequestParam(required = false) Long jdId,
            @RequestParam(required = false) InterviewStage stage,
            @RequestParam(required = false) ApplicationStatus status) {

        logger.info("HR searching candidates: jdId={}, stage={}, status={}", jdId, stage, status);
        List<CandidateResponseDTO> list = candidateService.searchCandidates(jdId, stage, status);
        logger.info("Found {} candidates matching filters", list.size());
        return new ApiResponseDTO<>(true, ApiConstants.CANDIDATES_FETCHED, list);
    }

    /**
     * HR: Delete a candidate by ID.
     * DELETE /candidates/{id}
     */
    @DeleteMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> deleteCandidate(@PathVariable Long id) {

        logger.info("Delete candidate request for id: {}", id);
        candidateService.deleteCandidate(id);
        logger.info("Candidate deleted: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.CANDIDATE_DELETED, null);
    }
}