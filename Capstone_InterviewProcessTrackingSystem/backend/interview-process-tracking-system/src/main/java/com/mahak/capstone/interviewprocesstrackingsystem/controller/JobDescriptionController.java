package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.JobDescriptionService;

import jakarta.validation.Valid;

/**
 * Controller for Job Description APIs.
 */
@RestController
@RequestMapping("/jobs")
public class JobDescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionController.class);

    private final JobDescriptionService jobService;

    public JobDescriptionController(JobDescriptionService jobService) {
        this.jobService = jobService;
    }

    /**
     * Create a new job (HR only)
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<JobResponseDTO> createJob(
            @Valid @RequestBody JobRequestDTO dto) {

        logger.info("Received request to create job: {}", dto.getTitle());

        JobResponseDTO response = jobService.createJob(dto);

        logger.info("Job created successfully: {}", response.getId());

        return new ApiResponseDTO<>(true, "Job created successfully", response);
    }
}