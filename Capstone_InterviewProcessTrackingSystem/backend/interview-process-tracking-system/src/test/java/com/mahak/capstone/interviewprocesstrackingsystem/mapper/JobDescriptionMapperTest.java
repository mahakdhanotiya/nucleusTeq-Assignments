package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.JobResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.JobType;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.JobDescriptionMapper;

public class JobDescriptionMapperTest {

    @Test
    void testToEntity() {
        JobRequestDTO dto = new JobRequestDTO();
        dto.setTitle("SDE");
        dto.setDescription("Java");
        dto.setJobType(JobType.FULL_TIME);
        
        JobDescription entity = JobDescriptionMapper.toEntity(dto);
        
        assertNotNull(entity);
        assertEquals("SDE", entity.getTitle());
    }

    @Test
    void testToResponse() {
        JobDescription entity = new JobDescription(1L, "SDE", "Desc", List.of("Java"), 1, 5, 1000.0, 5000.0, "Indore", JobType.FULL_TIME);
        entity.setIsActive(true);
        
        JobResponseDTO dto = JobDescriptionMapper.toResponse(entity);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("SDE", dto.getTitle());
    }
}
