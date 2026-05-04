package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.JobDescriptionMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.JobDescriptionService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.JobValidator;


@Service
public class JobDescriptionServiceImpl implements JobDescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionServiceImpl.class);

    private final JobDescriptionRepository jobRepository;

    public JobDescriptionServiceImpl(JobDescriptionRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Creates a new job with provided details.
     * Validates input and saves job to database.
     */

    @Override
    public JobResponseDTO createJob(JobRequestDTO dto) {

        logger.info("Creating job with title: {}", dto.getTitle());

        // Validation
        JobValidator.validateCreateJob(dto);

        // DTO → Entity
        JobDescription job = JobDescriptionMapper.toEntity(dto);

        // Save to DB
        JobDescription savedJob = jobRepository.save(job);

        logger.info("Job created successfully with id: {}", savedJob.getId());

        // Entity → Response DTO
        return JobDescriptionMapper.toResponse(savedJob);
    }


    /**
     * Fetches all active jobs from the system.
     * Returns list of job response DTOs.
     */
    @Override
    public List<JobResponseDTO> getAllJobs() {

        logger.info("Fetching all active jobs");

        List<JobResponseDTO> jobs = jobRepository.findAll()
        .stream()
        .map(JobDescriptionMapper::toResponse)
        .toList();

        logger.info("Total jobs fetched: {}", jobs.size());

        return jobs;
    }

    /**
     * Fetches only active jobs from the system.
     */
    @Override
    public List<JobResponseDTO> getAllActiveJobs() {

        logger.info("Fetching only active jobs");

        List<JobResponseDTO> jobs = jobRepository.findByIsActiveTrue()
        .stream()
        .map(JobDescriptionMapper::toResponse)
        .toList();

        logger.info("Total active jobs fetched: {}", jobs.size());

        return jobs;
    }

    /**
     * Deactivates a job by its ID.
     * Marks job as inactive if it exists.
     */
    
     @Override
    public void deactivateJob(Long id) {

        logger.info("Deactivating job with id: {}", id);

        JobDescription job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Job not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.JOB_NOT_FOUND);
                });
        if (Boolean.FALSE.equals(job.getIsActive())) {
            logger.error("Job already inactive with id: {}", id);
            throw new InvalidRequestException(ErrorConstants.JOB_ALREADY_INACTIVE);
        }
        job.setIsActive(false);
        jobRepository.save(job);

        logger.info("Job deactivated successfully: {}", id);
    }

    /**
     * Activates a job by its ID.
     * Marks job as active if it exists.
     */
    @Override
    public void activateJob(Long id) {

        logger.info("Activating job with id: {}", id);

        JobDescription job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Job not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.JOB_NOT_FOUND);
                });
        if (Boolean.TRUE.equals(job.getIsActive())) {
            logger.error("Job already active with id: {}", id);
            throw new InvalidRequestException("Job is already active");
        }
        job.setIsActive(true);
        jobRepository.save(job);

        logger.info("Job activated successfully: {}", id);
    }

    /**
     * Updates an existing job.
     */
    @Override
    public JobResponseDTO updateJob(Long id, JobRequestDTO dto) {

        logger.info("Updating job with id: {}", id);

        JobValidator.validateCreateJob(dto);

        JobDescription job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Job not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.JOB_NOT_FOUND);
                });

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkills(dto.getSkills());
        job.setMinExperience(dto.getMinExperience());
        job.setMaxExperience(dto.getMaxExperience());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());

        JobDescription savedJob = jobRepository.save(job);
        logger.info("Job updated successfully: {}", id);
        return JobDescriptionMapper.toResponse(savedJob);
    }
 }