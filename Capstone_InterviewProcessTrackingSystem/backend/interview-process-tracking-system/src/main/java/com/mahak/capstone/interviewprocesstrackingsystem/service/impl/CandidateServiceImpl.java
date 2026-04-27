package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.CandidateMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.CurrentUserUtil;
import com.mahak.capstone.interviewprocesstrackingsystem.service.CandidateService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.CandidateValidator;


/**
 * Service implementation class for Candidate operations.
 * Handles business logic related to CandidateProfile.
 */
@Service
public class CandidateServiceImpl implements CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateServiceImpl.class);

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final JobDescriptionRepository jobRepository;

    // Constructor Injection
    public CandidateServiceImpl(
            CandidateRepository candidateRepository,
            UserRepository userRepository,
            JobDescriptionRepository jobRepository) {

        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * Create new candidate application
     */
    @Override
    public CandidateResponseDTO createCandidate(CandidateRequestDTO requestDTO) {

        logger.info("Creating candidate for userId: {} and jobId: {}",
                requestDTO.getUserId(), requestDTO.getJobId());

        // Validate request
        CandidateValidator.validateCreateCandidate(requestDTO);

        // Fetch User
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", requestDTO.getUserId());
                    return new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND);
                });

        //Check active application 
        boolean exists = candidateRepository
                .existsByUserAndApplicationStatusNot(user, ApplicationStatus.REJECTED);

        if (exists) {
            logger.error("Active application already exists for userId: {}", requestDTO.getUserId());
            throw new InvalidRequestException(ErrorConstants.ACTIVE_APPLICATION_EXISTS);
        }

        // Fetch Job
        JobDescription job = jobRepository.findById(requestDTO.getJobId())
                .orElseThrow(() -> {
                    logger.error("Job not found with id: {}", requestDTO.getJobId());
                    return new ResourceNotFoundException(ErrorConstants.JOB_NOT_FOUND);
                });

        // Validate Resume (PDF only)
        if (!requestDTO.getResumeUrl().toLowerCase().endsWith(".pdf")) {
            logger.error("Invalid resume format for userId: {}", requestDTO.getUserId());
            throw new InvalidRequestException(ErrorConstants.INVALID_RESUME_FORMAT);
        }

        // Map DTO → Entity
        CandidateProfile candidate =
                CandidateMapper.toEntity(requestDTO, user, job);

        // Save in DB
        CandidateProfile saved = candidateRepository.save(candidate);

        logger.info("Candidate created successfully with id: {}", saved.getId());

        // Return Response DTO
        return CandidateMapper.toDTO(saved);
    }

    /**
     * Fetch all candidates
     */
    @Override
    public List<CandidateResponseDTO> getAllCandidates() {

        logger.info("Fetching all candidates");

        List<CandidateResponseDTO> list = candidateRepository.findAll()
                .stream()
                .map(CandidateMapper::toDTO)
                .toList();

        logger.info("Total candidates fetched: {}", list.size());

        return list;
    }

    /**
     * Fetch candidate by ID
     */
    @Override
    public CandidateResponseDTO getCandidateById(Long id) {

        logger.info("Fetching candidate with id: {}", id);

        CandidateProfile candidate = candidateRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorConstants.CANDIDATE_NOT_FOUND);
                });
        return CandidateMapper.toDTO(candidate);
    }

    

    @Override
    public CandidateResponseDTO getMyProfile() {

        logger.info("Fetching logged-in candidate profile");

        String email = CurrentUserUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND);
                });

        CandidateProfile candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> {
                    logger.error("Candidate not found for user: {}", user.getId());
                    return new ResourceNotFoundException(ErrorConstants.CANDIDATE_NOT_FOUND);
                });

        logger.info("Profile fetched successfully for user: {}", user.getId());

        return CandidateMapper.toDTO(candidate);
    }
}