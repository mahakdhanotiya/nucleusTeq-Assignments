package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * DTO for assigning panel to interview
 */
public class PanelAssignmentRequestDTO {

    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    @NotNull(message = "Panel ID is required")
    private Long panelId;

    @NotBlank(message = "Focus area is required")
    @Size(max = 300, message = "Focus area cannot exceed 300 characters")
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