package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.InterviewPanelAssignment;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;

public class InterviewPanelAssignmentMapperTest {

    private final InterviewPanelAssignmentMapper mapper = new InterviewPanelAssignmentMapper();

    @Test
    void testToEntity() {
        PanelAssignmentRequestDTO dto = new PanelAssignmentRequestDTO();
        dto.setFocusArea("Java");

        Interview interview = new Interview();
        org.springframework.test.util.ReflectionTestUtils.setField(interview, "id", 1L);
        
        PanelProfile panel = new PanelProfile();
        org.springframework.test.util.ReflectionTestUtils.setField(panel, "id", 2L);
        
        InterviewPanelAssignment entity = mapper.toEntity(dto, interview, panel);
        
        assertNotNull(entity);
        assertEquals(interview, entity.getInterview());
        assertEquals(panel, entity.getPanel());
        assertEquals("Java", entity.getFocusArea());
    }
}
