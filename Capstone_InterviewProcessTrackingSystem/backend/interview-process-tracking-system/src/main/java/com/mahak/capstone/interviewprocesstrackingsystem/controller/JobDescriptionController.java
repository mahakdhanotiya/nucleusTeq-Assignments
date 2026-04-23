package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * Creates a new job.
     * Accessible only by HR role.
     *
     * @param dto JobRequestDTO containing job details
     * @return ApiResponseDTO with created job data
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
    

    /**
     * Fetches all active jobs.
     *
     * @return list of active jobs
     */

    @GetMapping
    public ApiResponseDTO<List<JobResponseDTO>> getAllJobs() {

        logger.info("Fetching all jobs");

        List<JobResponseDTO> jobs = jobService.getAllJobs();

        return new ApiResponseDTO<>(true, "Jobs fetched successfully", jobs);
    }


    /**
     * Deactivates a job by ID.
     *
     * @param id job id
     * @return success response
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> deactivateJob(@PathVariable Long id) {

        logger.info("Received request to deactivate job: {}", id);

        jobService.deactivateJob(id);

        return new ApiResponseDTO<>(true, "Job deactivated successfully", null);
    }
}