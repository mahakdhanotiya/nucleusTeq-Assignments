package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;


/**
 * Validator class for CandidateProfile
 * Contains validation logic before saving data
 */
public class CandidateValidator {

    private static final Logger logger = LoggerFactory.getLogger(CandidateValidator.class);
    
    /**
     * Validates candidate data before creation.
     *
     * @param dto CandidateRequestDTO containing candidate details
     * @throws InvalidRequestException if validation fails
     */
    
    public static void validateCreateCandidate(CandidateRequestDTO dto) {

        if (Objects.isNull(dto)) {
            logger.error("Validation failed: Request body is null");
            throw new InvalidRequestException("Request body cannot be null");
        }

        if (Objects.isNull(dto.getMobileNumber()) || dto.getMobileNumber().isBlank()) {
            logger.error("Validation failed: Mobile number is required");
            throw new InvalidRequestException("Mobile number is required");
        }

        if (Objects.isNull(dto.getResumePath()) || dto.getResumePath().isBlank()) {
            logger.error("Validation failed: Resume path is required");
            throw new InvalidRequestException("Resume path is required");
        }

        if (Objects.isNull(dto.getTotalExperience()) || dto.getTotalExperience() < 0) {
            logger.error("Validation failed: Total experience is invalid");
            throw new InvalidRequestException("Total experience must be valid");
        }

        if (Objects.isNull(dto.getUserId())) {
            logger.error("Validation failed: User ID is required");
            throw new InvalidRequestException("User ID is required");
        }

        if (Objects.isNull(dto.getJobId())) {
            logger.error("Validation failed: Job ID is required");
            throw new InvalidRequestException("Job ID is required");
        }

        // logical validation
        if (!Objects.isNull(dto.getRelevantExperience()) &&
            dto.getRelevantExperience() > dto.getTotalExperience()) {
               logger.error("Validation failed: Relevant experience exceeds total experience"); 
            
               throw new InvalidRequestException("Relevant experience cannot exceed total experience");
        }
    }
}