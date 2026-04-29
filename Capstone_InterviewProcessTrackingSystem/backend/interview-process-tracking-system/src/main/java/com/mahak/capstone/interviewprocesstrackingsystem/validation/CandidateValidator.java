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
     * Validates candidate data before creation
     */
    public static void validateCreateCandidate(CandidateRequestDTO dto) {

        if (Objects.isNull(dto)) {
            logger.error("Validation failed: Request body is null");
            throw new InvalidRequestException(ErrorConstants.INVALID_REQUEST);
        }

        // logical validation: relevant exp <= total exp
        if (!Objects.isNull(dto.getRelevantExperience()) &&
                dto.getRelevantExperience() > dto.getTotalExperience()) {

            logger.error("Validation failed: Relevant experience exceeds total experience");

            throw new InvalidRequestException(
                    ErrorConstants.CANDIDATE_INVALID_EXPERIENCE_RANGE
            );
        }
    }
}