package com.mahak.capstone.interviewprocesstrackingsystem.dto;

/**
 * DTO for assigning panel to interview
 */
public class PanelAssignmentRequestDTO {

    private Long interviewId;
    private Long panelId;
    private String focusArea;

    // Getters & Setters

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }
}