package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;


/**
 * Validator class for CandidateProfile
 * Contains validation logic before saving data
 */
public class CandidateValidator {

    /**
     * Validates candidate data
     */
    public static void validateCreateCandidate(CandidateRequestDTO dto) {

        if (dto == null) {
            throw new RuntimeException("Request body cannot be null");
        }

        if (dto.getMobileNumber() == null || dto.getMobileNumber().isBlank()) {
            throw new RuntimeException("Mobile number is required");
        }

        if (dto.getResumePath() == null || dto.getResumePath().isBlank()) {
            throw new RuntimeException("Resume path is required");
        }

        if (dto.getTotalExperience() == null || dto.getTotalExperience() < 0) {
            throw new RuntimeException("Total experience must be valid");
        }

        if (dto.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }

        if (dto.getJobId() == null) {
            throw new RuntimeException("Job ID is required");
        }

        // logical validation
        if (dto.getRelevantExperience() != null &&
            dto.getRelevantExperience() > dto.getTotalExperience()) {
            throw new RuntimeException("Relevant experience cannot exceed total experience");
        }
    }
}