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
     * Fetches only active jobs (for public/candidate use).
     */
    List<JobResponseDTO> getAllActiveJobs();


    /**
     * Deactivates a job by ID.
     */
   void deactivateJob(Long id);

    /**
     * Activates a job by ID.
     */
   void activateJob(Long id);

   /**
    * Updates an existing job by ID.
    */
   JobResponseDTO updateJob(Long id, JobRequestDTO dto);

}