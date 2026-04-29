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

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.service.JobDescriptionService;

import jakarta.validation.Valid;

/**
 * Controller for Job Description APIs.
 */
@RestController
@RequestMapping(ApiConstants.JOBS)
public class JobDescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionController.class);

    private final JobDescriptionService jobService;

    public JobDescriptionController(JobDescriptionService jobService) {
        this.jobService = jobService;
    }

    /**
     * Creates a new job. HR only.
     * POST /jobs
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<JobResponseDTO> createJob(
            @Valid @RequestBody JobRequestDTO dto) {

        logger.info("Create job request received: {}", dto.getTitle());
        JobResponseDTO response = jobService.createJob(dto);
        logger.info("Job created successfully with id: {}", response.getId());
        return new ApiResponseDTO<>(true, ApiConstants.JOB_CREATED, response);
    }

    /**
     * Fetches all active jobs.
     * GET /jobs
     */
    @GetMapping
    public ApiResponseDTO<List<JobResponseDTO>> getAllJobs() {

        logger.info("Fetching all active jobs");
        List<JobResponseDTO> jobs = jobService.getAllActiveJobs();
        logger.info("Fetched {} jobs", jobs.size());
        return new ApiResponseDTO<>(true, ApiConstants.JOBS_FETCHED, jobs);
    }

    /**
     * Fetches all jobs (including inactive) for HR dashboard.
     * GET /jobs/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<List<JobResponseDTO>> getAllJobsForHR() {

        logger.info("Fetching all jobs for HR (including inactive)");
        List<JobResponseDTO> jobs = jobService.getAllJobs();
        logger.info("Fetched {} total jobs", jobs.size());
        return new ApiResponseDTO<>(true, ApiConstants.JOBS_FETCHED, jobs);
    }

    /**
     * Deactivates a job by ID. HR only.
     * PUT /jobs/{id}/deactivate
     */
    @PutMapping(ApiConstants.DEACTIVATE)
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> deactivateJob(@PathVariable Long id) {

        logger.info("Deactivate job request received for id: {}", id);
        jobService.deactivateJob(id);
        logger.info("Job deactivated successfully: {}", id);
        return new ApiResponseDTO<>(true, ApiConstants.JOB_DEACTIVATED, null);
    }

    /**
     * Activates a job by ID. HR only.
     * PUT /jobs/{id}/activate
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<Void> activateJob(@PathVariable Long id) {

        logger.info("Activate job request received for id: {}", id);
        jobService.activateJob(id);
        logger.info("Job activated successfully: {}", id);
        return new ApiResponseDTO<>(true, "Job activated successfully", null);
    }

    /**
     * Updates an existing job by ID. HR only.
     * PUT /jobs/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ApiResponseDTO<JobResponseDTO> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequestDTO dto) {

        logger.info("Update job request received for id: {}", id);
        JobResponseDTO response = jobService.updateJob(id, dto);
        logger.info("Job updated successfully: {}", id);
        return new ApiResponseDTO<>(true, "Job updated successfully", response);
    }
}