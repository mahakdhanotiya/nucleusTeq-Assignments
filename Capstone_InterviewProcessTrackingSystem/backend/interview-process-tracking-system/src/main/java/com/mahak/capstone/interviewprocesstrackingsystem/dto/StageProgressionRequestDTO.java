package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** DTO for HR to manually advance a candidate's interview stage */
public class StageProgressionRequestDTO {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotBlank(message = "New stage is required (SCREENING, L1, L2, HR, SELECTED, REJECTED)")
    private String newStage;

    private String hrComments;  // mandatory for HR round final decision

    // Getters & Setters
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getNewStage() { return newStage; }
    public void setNewStage(String newStage) { this.newStage = newStage; }

    public String getHrComments() { return hrComments; }
    public void setHrComments(String hrComments) { this.hrComments = hrComments; }
}