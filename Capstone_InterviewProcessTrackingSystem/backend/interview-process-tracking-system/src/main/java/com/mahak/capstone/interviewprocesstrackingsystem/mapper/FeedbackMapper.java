package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import org.springframework.stereotype.Component;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackDetailResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.FeedbackResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.FeedbackStatus;

@Component
public class FeedbackMapper {

    /**
     * RequestDTO → Entity
     */
    public Feedback toEntity(FeedbackRequestDTO dto,
            Interview interview,
            PanelProfile panel) {

        Feedback feedback = new Feedback();

        feedback.setInterview(interview);
        feedback.setPanel(panel);
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());
        feedback.setStrengths(dto.getStrengths());
        feedback.setWeaknesses(dto.getWeaknesses());
        feedback.setAreasCovered(dto.getAreasCovered());
        feedback.setStatus(FeedbackStatus.valueOf(dto.getStatus()));

        return feedback;
    }

    /**
     * Entity → Summary DTO
     */
    public FeedbackResponseDTO toResponseDTO(Feedback feedback) {

        FeedbackResponseDTO dto = new FeedbackResponseDTO();

        dto.setId(feedback.getId());
        dto.setRating(feedback.getRating());
        dto.setStatus(feedback.getStatus().name());
        dto.setComments(feedback.getComments());
        dto.setStrengths(feedback.getStrengths());
        dto.setWeaknesses(feedback.getWeaknesses());
        dto.setAreasCovered(feedback.getAreasCovered());

        // Interview & Candidate Context
        if (feedback.getInterview() != null) {
            dto.setInterviewId(feedback.getInterview().getId());
            dto.setInterviewStage(feedback.getInterview().getStage().name());

            if (feedback.getInterview().getCandidate() != null) {
                dto.setCandidateId(feedback.getInterview().getCandidate().getId());
                if (feedback.getInterview().getCandidate().getUser() != null) {
                    dto.setCandidateName(feedback.getInterview().getCandidate().getUser().getFullName());
                }
            }
        }

        // Panel Context
        if (feedback.getPanel() != null) {
            dto.setPanelId(feedback.getPanel().getId());
            if (feedback.getPanel().getUser() != null) {
                dto.setPanelName(feedback.getPanel().getUser().getFullName());
            }
        } else {
            // Smart Labeling for Legacy/HR Data
            if (feedback.getInterview() != null && "HR".equals(feedback.getInterview().getStage().name())) {
                dto.setPanelName("HR Manager");
            } else {
                dto.setPanelName("Panelist Evaluation");
            }
        }

        return dto;
    }

    /**
     * Entity → Detailed DTO
     */
    public FeedbackDetailResponseDTO toDetailDTO(Feedback feedback) {

        FeedbackDetailResponseDTO dto = new FeedbackDetailResponseDTO();

        dto.setId(feedback.getId());
        dto.setRating(feedback.getRating());
        dto.setStatus(feedback.getStatus().name());

        dto.setComments(feedback.getComments());
        dto.setStrengths(feedback.getStrengths());
        dto.setWeaknesses(feedback.getWeaknesses());
        dto.setAreasCovered(feedback.getAreasCovered());

        // panel name
        if (feedback.getPanel() != null &&
                feedback.getPanel().getUser() != null) {

            dto.setPanelName(
                    feedback.getPanel().getUser().getFullName());
        } else {
            if (feedback.getInterview() != null && "HR".equals(feedback.getInterview().getStage().name())) {
                dto.setPanelName("HR Manager");
            } else {
                dto.setPanelName("Panelist Evaluation");
            }
        }

        return dto;
    }
}