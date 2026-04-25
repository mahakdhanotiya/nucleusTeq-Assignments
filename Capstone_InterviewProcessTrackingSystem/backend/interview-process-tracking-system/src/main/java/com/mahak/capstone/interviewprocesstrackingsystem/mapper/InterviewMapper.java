package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

@Component
public class InterviewMapper {

    /**
     * Convert RequestDTO → Entity
     */
    public Interview toEntity(InterviewRequestDTO dto,
                              CandidateProfile candidate,
                              JobDescription jd) {

        Interview interview = new Interview();

        interview.setInterviewDateTime(dto.getInterviewDateTime());
        interview.setStage(InterviewStage.valueOf(dto.getStage()));
        interview.setFocusArea(dto.getFocusArea());
        interview.setCandidate(candidate);
        interview.setJobDescription(jd);

        return interview;
    }

    /**
     * Convert Entity → ResponseDTO
     */
    public InterviewResponseDTO toResponseDTO(Interview interview) {

        InterviewResponseDTO dto = new InterviewResponseDTO();

        dto.setId(interview.getId());
        dto.setInterviewDateTime(interview.getInterviewDateTime());
        dto.setStage(interview.getStage().name());
        dto.setStatus(interview.getStatus().name());
        dto.setFocusArea(interview.getFocusArea());

        // Candidate name (safe null check)
        if (interview.getCandidate() != null &&
            interview.getCandidate().getUser() != null) {

            dto.setCandidateName(
                interview.getCandidate().getUser().getFullName()
            );
        }

        return dto;
    }
}