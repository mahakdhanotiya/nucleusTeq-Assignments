package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;

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
        
        // relations → IDs
        dto.setUserId(c.getUser().getId());
        dto.setJobId(c.getJobDescription().getId());

        return dto;
    }
}