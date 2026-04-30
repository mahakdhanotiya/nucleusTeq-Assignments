package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

/**
 * Validator class for CandidateProfile
 * Contains business validation logic
 */
public class CandidateValidator {

    private static final Logger logger = LoggerFactory.getLogger(CandidateValidator.class);

    /**
     * Validates candidate data before creation or update
     */
    public static void validateCreateCandidate(CandidateRequestDTO dto) {

        if (Objects.isNull(dto)) {
            logger.error("Validation failed: Request body is null");
            throw new InvalidRequestException(ErrorConstants.INVALID_REQUEST);
        }

        // 1. Experience Validations
        if (dto.getTotalExperience() < 0) {
            throw new InvalidRequestException("Total experience cannot be negative");
        }
        if (dto.getRelevantExperience() != null && dto.getRelevantExperience() < 0) {
            throw new InvalidRequestException("Relevant experience cannot be negative");
        }
        if (dto.getRelevantExperience() != null && dto.getRelevantExperience() > dto.getTotalExperience()) {
            logger.error("Validation failed: Relevant experience exceeds total experience");
            throw new InvalidRequestException(ErrorConstants.CANDIDATE_INVALID_EXPERIENCE_RANGE);
        }

        // 2. Mobile Number Validation (Simple 10-digit check)
        if (dto.getMobileNumber() != null && !dto.getMobileNumber().matches("^[0-9]{10}$")) {
            throw new InvalidRequestException("Mobile number must be exactly 10 digits");
        }

        // 3. CTC Validations
        if (dto.getCurrentCTC() != null && dto.getCurrentCTC() < 0) {
            throw new InvalidRequestException("Current CTC cannot be negative");
        }
        if (dto.getExpectedCTC() != null && dto.getExpectedCTC() < 0) {
            throw new InvalidRequestException("Expected CTC cannot be negative");
        }

        // 4. Resume Validation
        if (dto.getResumeUrl() == null || dto.getResumeUrl().isBlank()) {
            throw new InvalidRequestException("Resume URL is required");
        }
        if (!dto.getResumeUrl().toLowerCase().endsWith(".pdf")) {
            throw new InvalidRequestException(ErrorConstants.INVALID_RESUME_FORMAT);
        }
    }
}