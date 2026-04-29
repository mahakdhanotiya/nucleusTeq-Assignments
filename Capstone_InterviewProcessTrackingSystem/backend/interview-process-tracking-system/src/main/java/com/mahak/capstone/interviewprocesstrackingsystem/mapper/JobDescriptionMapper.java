package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;

/**
 * Mapper class for converting between JobDescription entity and DTOs.
 */
public class JobDescriptionMapper {

    /**
     * Converts JobRequestDTO to JobDescription entity.
     *
     * @param dto the job request data
     * @return JobDescription entity
     */
    public static JobDescription toEntity(JobRequestDTO dto) {

        JobDescription job = new JobDescription();

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkills(dto.getSkills());
        job.setMinExperience(dto.getMinExperience());
        job.setMaxExperience(dto.getMaxExperience());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());

        return job;
    }

    /**
     * Converts JobDescription entity to JobResponseDTO.
     *
     * @param job the job entity
     * @return JobResponseDTO
     */
    public static JobResponseDTO toResponse(JobDescription job) {

        JobResponseDTO dto = new JobResponseDTO();

        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setSkills(job.getSkills());
        dto.setMinExperience(job.getMinExperience());
        dto.setMaxExperience(job.getMaxExperience());
        dto.setMinSalary(job.getMinSalary());
        dto.setMaxSalary(job.getMaxSalary());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setIsActive(job.getIsActive());

        return dto;
    }
}