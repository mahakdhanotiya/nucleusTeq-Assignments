package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for detailed interview details in dashboards
 */
public class InterviewResponseDTO {

    private Long id;
    private Long candidateId;
    private String candidateName;
    private String jobTitle;
    private String stage;
    private String status;
    private LocalDateTime interviewDateTime;
    private String focusArea;
    private String candidateCurrentStatus;
    private String candidateCurrentStage;
    private List<String> assignedPanelNames = new ArrayList<>();
    private List<Long> assignedPanelIds = new ArrayList<>();
    private List<String> assignedPanelFocusAreas = new ArrayList<>();
    private List<Long> feedbackProvidedBy = new ArrayList<>(); // IDs of panels who gave feedback, null for HR
    private String candidateResumeUrl;
    private String candidateEmail;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public String getCandidateCurrentStatus() {
        return candidateCurrentStatus;
    }

    public void setCandidateCurrentStatus(String candidateCurrentStatus) {
        this.candidateCurrentStatus = candidateCurrentStatus;
    }

    public String getCandidateCurrentStage() {
        return candidateCurrentStage;
    }

    public void setCandidateCurrentStage(String candidateCurrentStage) {
        this.candidateCurrentStage = candidateCurrentStage;
    }

    public List<String> getAssignedPanelNames() {
        return assignedPanelNames;
    }

    public void setAssignedPanelNames(List<String> assignedPanelNames) {
        this.assignedPanelNames = assignedPanelNames;
    }

    public List<Long> getAssignedPanelIds() {
        return assignedPanelIds;
    }

    public void setAssignedPanelIds(List<Long> assignedPanelIds) {
        this.assignedPanelIds = assignedPanelIds;
    }

    public List<String> getAssignedPanelFocusAreas() {
        return assignedPanelFocusAreas;
    }

    public void setAssignedPanelFocusAreas(List<String> assignedPanelFocusAreas) {
        this.assignedPanelFocusAreas = assignedPanelFocusAreas;
    }

    public List<Long> getFeedbackProvidedBy() {
        return feedbackProvidedBy;
    }

    public void setFeedbackProvidedBy(List<Long> feedbackProvidedBy) {
        this.feedbackProvidedBy = feedbackProvidedBy;
    }

    public String getCandidateResumeUrl() { return candidateResumeUrl; }
    public void setCandidateResumeUrl(String candidateResumeUrl) { this.candidateResumeUrl = candidateResumeUrl; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
}