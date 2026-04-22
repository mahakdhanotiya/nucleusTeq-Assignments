package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.CandidateMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.CandidateService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.CandidateValidator;


/**
 * Service implementation class for Candidate operations.
 * Handles business logic related to CandidateProfile.
 */
@Service
public class CandidateServiceImpl implements CandidateService {

    // Repository to interact with database
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final JobDescriptionRepository jobRepository;

    
    // constructor injection
    public CandidateServiceImpl(
            CandidateRepository candidateRepository,
            UserRepository userRepository,
            JobDescriptionRepository jobRepository) {

        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }
    
    /**
     * Creates and saves a new candidate profile in the database
     *
     * @param candidate CandidateProfile object containing candidate details
     * @return saved CandidateProfile
     */
    
    @Override
    public CandidateResponseDTO createCandidate(CandidateRequestDTO requestDTO) {

        // validate request before processing
        CandidateValidator.validateCreateCandidate(requestDTO);

        // get user from DB
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // get job from DB
        JobDescription job = jobRepository.findById(requestDTO.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // convert DTO → Entity
        CandidateProfile candidate = CandidateMapper.toEntity(requestDTO, user, job);

        // save in DB
        CandidateProfile saved = candidateRepository.save(candidate);

        // convert Entity → ResponseDTO
        return CandidateMapper.toDTO(saved);
    }
}