package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.FeedbackStatus;

public class FeedbackMapperTest {

    private final FeedbackMapper mapper = new FeedbackMapper();

    @Test
    void testToEntity() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setComments("Good");
        dto.setRating(5);
        dto.setStatus("SELECTED");
        
        Interview interview = new Interview();
        org.springframework.test.util.ReflectionTestUtils.setField(interview, "id", 1L);
        
        PanelProfile panel = new PanelProfile();
        org.springframework.test.util.ReflectionTestUtils.setField(panel, "id", 1L);
        
        Feedback entity = mapper.toEntity(dto, interview, panel);
        
        assertNotNull(entity);
        assertEquals("Good", entity.getComments());
        assertEquals(interview, entity.getInterview());
    }

    @Test
    void testToDTO() {
        Feedback entity = new Feedback();
        org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", 1L);
        entity.setComments("Good");
        entity.setStatus(FeedbackStatus.SELECTED);
        
        Interview interview = new Interview();
        org.springframework.test.util.ReflectionTestUtils.setField(interview, "id", 1L);
        interview.setStage(InterviewStage.L1);
        entity.setInterview(interview);
        
        FeedbackResponseDTO dto = mapper.toResponseDTO(entity);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getInterviewId());
    }
}
