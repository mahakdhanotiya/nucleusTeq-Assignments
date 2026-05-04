package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

/**
 * Validator for Job Description operations.
 */
public class JobValidator {

    private static final Logger logger = LoggerFactory.getLogger(JobValidator.class);

    public static void validateCreateJob(JobRequestDTO dto) {

        if (!Objects.isNull(dto.getMinExperience()) &&
            !Objects.isNull(dto.getMaxExperience()) &&
            dto.getMinExperience() > dto.getMaxExperience()) {

            logger.error("Validation failed: minExperience > maxExperience");
            throw new InvalidRequestException(ErrorConstants.INVALID_EXPERIENCE_RANGE);
        }
        

        if (!Objects.isNull(dto.getMinSalary()) && (!Objects.isNull(dto.getMaxSalary()))
                && dto.getMinSalary() > dto.getMaxSalary()) {

            logger.error("Validation failed: minSalary > maxSalary");
            throw new InvalidRequestException(ErrorConstants.INVALID_SALARY_RANGE);
        }
    }
}