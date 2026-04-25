package com.mahak.capstone.interviewprocesstrackingsystem.controller;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.InterviewService;

/**
 * REST Controller for Interview operations
 * 
 * Role Access:
 * - HR → schedule + assign panel
 * - Candidate → view interview
 */
@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /**
     * HR: Schedule interview
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(
            @RequestBody InterviewRequestDTO dto) {

                System.out.println("==== DEBUG START ====");
    System.out.println("DTO: " + dto);
    System.out.println("candidateId: " + dto.getCandidateId());
    System.out.println("jdId: " + dto.getJobDescriptionId());
    System.out.println("date: " + dto.getInterviewDateTime());
    System.out.println("==== DEBUG END ====");


        logger.info("HR scheduling interview");

        InterviewResponseDTO response = interviewService.scheduleInterview(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * HR: Assign panel
     */
    @PostMapping("/assign-panel")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> assignPanel(
            @RequestBody PanelAssignmentRequestDTO dto) {

        logger.info("HR assigning panel");

        interviewService.assignPanel(dto);

        return ResponseEntity.ok("Panel assigned successfully");
    }

    /**
     * Candidate / Panel / HR: View interview
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR','PANEL','CANDIDATE')")
    public ResponseEntity<InterviewResponseDTO> getInterviewById(
            @PathVariable Long id) {

        logger.info("Fetching interview details for id: {}", id);

        InterviewResponseDTO response = interviewService.getInterviewById(id);

        return ResponseEntity.ok(response);
    }


    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<InterviewResponseDTO>> getAllInterviews() {

        logger.info("HR fetching all interviews");

        List<InterviewResponseDTO> response = interviewService.getAllInterviews();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAnyRole('HR','CANDIDATE')")
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByCandidate(
            @PathVariable Long candidateId) {

        logger.info("Fetching interviews for candidateId: {}", candidateId);

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByCandidate(candidateId);

        return ResponseEntity.ok(response);
    }
}

