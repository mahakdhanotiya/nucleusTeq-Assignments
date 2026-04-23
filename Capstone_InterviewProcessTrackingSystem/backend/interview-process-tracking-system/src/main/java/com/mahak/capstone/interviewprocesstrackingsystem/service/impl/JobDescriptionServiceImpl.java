package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
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
}