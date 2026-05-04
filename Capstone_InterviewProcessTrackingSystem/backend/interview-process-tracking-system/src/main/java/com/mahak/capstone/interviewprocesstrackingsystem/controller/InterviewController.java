package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewUpdateDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.StageProgressionRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.service.InterviewService;

import jakarta.validation.Valid;

/**
 * REST Controller for Interview operations.
 *
 * Role Access:
 * - HR → schedule, assign panel, view all
 * - Candidate → view own interviews
 */
@RestController
@RequestMapping(ApiConstants.INTERVIEWS)
public class InterviewController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /**
     * HR: Schedule interview.
     * POST /api/interviews
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<InterviewResponseDTO> scheduleInterview(
            @RequestBody InterviewRequestDTO dto) {

        logger.info("Schedule interview request received for candidateId: {}", dto.getCandidateId());
        InterviewResponseDTO response = interviewService.scheduleInterview(dto);
        logger.info("Interview scheduled successfully with id: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEW_SCHEDULED, response);
    }

    /**
     * HR: Assign panel to interview.
     * POST /api/interviews/assign-panel
     */
    @PostMapping(ApiConstants.ASSIGN_PANEL)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> assignPanel(
            @RequestBody PanelAssignmentRequestDTO dto) {

        logger.info("Assign panel request: interviewId={}, panelId={}", dto.getInterviewId(), dto.getPanelId());
        interviewService.assignPanel(dto);
        logger.info("Panel assigned successfully to interviewId: {}", dto.getInterviewId());
        return new ApiResponseDTO<>(true, ApiConstants.PANEL_ASSIGNED, null);
    }

    /**
     * View interview by ID.
     * GET /api/interviews/{id}
     */
    @GetMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasAnyRole('HR','PANEL','CANDIDATE')")
    public ApiResponseDTO<InterviewResponseDTO> getInterviewById(
            @PathVariable Long id) {

        logger.info("Fetching interview with id: {}", id);
        InterviewResponseDTO response = interviewService.getInterviewById(id);
        logger.info("Interview fetched: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEW_FETCHED, response);
    }

    /**
     * HR & PANEL: Get all interviews.
     * GET /api/interviews
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('HR','PANEL')")
    public ApiResponseDTO<List<InterviewResponseDTO>> getAllInterviews() {

        logger.info("Fetching interviews");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = "USER";
        String username = null;
        if (auth != null && !auth.getAuthorities().isEmpty()) {
            role = auth.getAuthorities().iterator().next().getAuthority();
            
            Object principal = auth.getPrincipal();
            if (principal instanceof com.mahak.capstone.interviewprocesstrackingsystem.entity.User) {
                username = ((com.mahak.capstone.interviewprocesstrackingsystem.entity.User) principal).getEmail();
            } else if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = auth.getName();
            }
        }

        List<InterviewResponseDTO> response = interviewService.getAllInterviews(role, username);
        logger.info("Fetched {} interviews for role {}", response.size(), role);
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEWS_FETCHED, response);
    }

    /**
     * Get interviews for a candidate.
     * GET /api/interviews/candidate/{candidateId}
     */
    @GetMapping(ApiConstants.BY_CANDIDATE)
    @PreAuthorize("hasAnyRole('HR','CANDIDATE')")
    public ApiResponseDTO<List<InterviewResponseDTO>> getInterviewsByCandidate(
            @PathVariable Long candidateId) {

        logger.info("Fetching interviews for candidateId: {}", candidateId);
        List<InterviewResponseDTO> response = interviewService.getInterviewsByCandidate(candidateId);
        logger.info("Fetched {} interviews for candidateId: {}", response.size(), candidateId);
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEWS_FETCHED, response);
    }

    /**
     * HR: Advance candidate to next stage or reject.
     * POST /api/interviews/stage-progression
     */
    @PostMapping(ApiConstants.STAGE_PROGRESSION)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<CandidateResponseDTO> progressStage(
            @Valid @RequestBody StageProgressionRequestDTO dto) {

        logger.info("Stage progression request: candidateId={}, newStage={}", dto.getCandidateId(), dto.getNewStage());
        CandidateResponseDTO response = interviewService.progressCandidateStage(dto);
        logger.info("Stage updated for candidateId: {}", dto.getCandidateId());
        return new ApiResponseDTO<>(true, ApiConstants.STAGE_UPDATED, response);
    }

    /**
     * HR: Update interview status (CANCELLED, NO_SHOW, etc.)
     * PUT /api/interviews/{id}/status
     */
    @PutMapping(ApiConstants.STATUS)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam InterviewStatus status) {

        logger.info("Update status request: interviewId={}, status={}", id, status);
        interviewService.updateInterviewStatus(id, status);
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEW_STATUS_UPDATED, null);
    }

    /**
     * HR: Update interview details.
     * PUT /api/interviews/{id}
     */
    @PutMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<InterviewResponseDTO> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody InterviewUpdateDTO dto) {

        logger.info("Update interview request for id: {}", id);
        InterviewResponseDTO response = interviewService.updateInterview(id, dto);
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEW_UPDATED, response);
    }

    /**
     * HR: Delete interview by ID.
     */
    @DeleteMapping(ApiConstants.BY_ID)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> deleteInterview(@PathVariable Long id) {

        logger.info("Delete interview request for id: {}", id);
        interviewService.deleteInterview(id);
        logger.info("Interview deleted: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.INTERVIEW_DELETED, null);
    }
}