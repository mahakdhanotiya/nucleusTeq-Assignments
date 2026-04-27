package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.InterviewPanelAssignment;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;

@Component
public class InterviewPanelAssignmentMapper {

    /**
     * Convert RequestDTO → Entity
     */
    public InterviewPanelAssignment toEntity(PanelAssignmentRequestDTO dto,
                                              Interview interview,
                                              PanelProfile panel) {

        InterviewPanelAssignment assignment = new InterviewPanelAssignment();

        assignment.setInterview(interview);
        assignment.setPanel(panel);
        assignment.setFocusArea(dto.getFocusArea());

        return assignment;
    }
}