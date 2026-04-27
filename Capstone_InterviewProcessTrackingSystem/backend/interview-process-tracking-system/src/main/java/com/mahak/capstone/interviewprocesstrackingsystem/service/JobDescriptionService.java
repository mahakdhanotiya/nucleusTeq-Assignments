package com.mahak.capstone.interviewprocesstrackingsystem.service;

import java.util.List;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;

/**
 * Service interface for Job Description operations.
 */
public interface JobDescriptionService {

    /**
     * Creates a new job description.
     *
     * @param dto JobRequestDTO containing job details
     * @return JobResponseDTO of created job
     */
    JobResponseDTO createJob(JobRequestDTO dto);

    /**
     * Fetches all active jobs.
     */
    List<JobResponseDTO> getAllJobs();


    /**
     * Deactivates a job by ID.
     */
   void deactivateJob(Long id);

}