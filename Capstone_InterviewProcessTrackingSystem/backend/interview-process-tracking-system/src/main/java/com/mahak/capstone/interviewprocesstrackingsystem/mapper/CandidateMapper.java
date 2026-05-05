package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import java.time.LocalDateTime;

public class CandidateMapper {

    /**
     * Convert RequestDTO → Entity
     */
    public static CandidateProfile toEntity(
            CandidateRequestDTO dto,
            User user,
            JobDescription job) {

        CandidateProfile c = new CandidateProfile();

        c.setMobileNumber(dto.getMobileNumber());
        c.setResumeUrl(dto.getResumeUrl());
        c.setCurrentCompany(dto.getCurrentCompany());

        c.setTotalExperience(dto.getTotalExperience());
        c.setRelevantExperience(dto.getRelevantExperience());

        c.setCurrentCTC(dto.getCurrentCTC());
        c.setExpectedCTC(dto.getExpectedCTC());

        c.setNoticePeriod(dto.getNoticePeriod());
        c.setPreferredLocation(dto.getPreferredLocation());

        c.setSource(dto.getSource());
        

        // relations
        c.setUser(user);
        c.setJobDescription(job);

        // Manually set defaults in case @PrePersist fails
        c.setCreatedAt(LocalDateTime.now());
        c.setCurrentStage(InterviewStage.PROFILING);
        c.setApplicationStatus(ApplicationStatus.PROFILING_COMPLETED);

        return c;
    }

    /**
     * Convert Entity → ResponseDTO
     */
    public static CandidateResponseDTO toDTO(CandidateProfile c) {

        CandidateResponseDTO dto = new CandidateResponseDTO();

        dto.setId(c.getId());

        dto.setMobileNumber(c.getMobileNumber());
        dto.setResumeUrl(c.getResumeUrl());
        dto.setCurrentCompany(c.getCurrentCompany());

        dto.setTotalExperience(c.getTotalExperience());
        dto.setRelevantExperience(c.getRelevantExperience());

        dto.setCurrentCTC(c.getCurrentCTC());
        dto.setExpectedCTC(c.getExpectedCTC());

        dto.setNoticePeriod(c.getNoticePeriod());
        dto.setPreferredLocation(c.getPreferredLocation());

        dto.setSource(c.getSource());
        dto.setCurrentStage(c.getCurrentStage());
        dto.setApplicationStatus(c.getApplicationStatus());
        
        dto.setCreatedAt(c.getCreatedAt());
        
        // relations → IDs + names (Null-Safe)
        if (c.getUser() != null) {
            dto.setUserId(c.getUser().getId());
            dto.setFullName(c.getUser().getFullName());
            dto.setEmail(c.getUser().getEmail());
        }
        
        if (c.getJobDescription() != null) {
            dto.setJobId(c.getJobDescription().getId());
            dto.setJobTitle(c.getJobDescription().getTitle());
        }
        
        return dto;
    }
}