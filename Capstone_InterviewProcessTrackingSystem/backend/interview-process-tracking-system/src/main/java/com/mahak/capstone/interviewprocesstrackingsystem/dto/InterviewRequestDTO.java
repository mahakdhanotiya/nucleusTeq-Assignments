package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for scheduling interview
 */

    public class InterviewRequestDTO {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "Job Description ID is required")
    private Long jobDescriptionId;

    @NotNull(message = "Interview date & time is required")
    private LocalDateTime interviewDateTime;

    @NotBlank(message = "Stage is required")
    private String stage;

    @NotBlank(message = "Focus area is required")
    @Size(max = 500, message = "Focus area cannot exceed 500 characters")
    private String focusArea;

    // getters setters


    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobDescriptionId() {
        return jobDescriptionId;
    }

    public void setJobDescriptionId(Long jobDescriptionId) {
        this.jobDescriptionId = jobDescriptionId;
    }

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }
}