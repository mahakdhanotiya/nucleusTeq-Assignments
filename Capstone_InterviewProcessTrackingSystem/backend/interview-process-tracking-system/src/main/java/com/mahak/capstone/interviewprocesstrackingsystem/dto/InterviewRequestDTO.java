package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import java.time.LocalDateTime;

/**
 * DTO for scheduling interview
 */
public class InterviewRequestDTO {

    private Long candidateId;
    private Long jobDescriptionId;
    private LocalDateTime interviewDateTime;
    private String stage;
    private String focusArea;

    // Getters & Setters

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