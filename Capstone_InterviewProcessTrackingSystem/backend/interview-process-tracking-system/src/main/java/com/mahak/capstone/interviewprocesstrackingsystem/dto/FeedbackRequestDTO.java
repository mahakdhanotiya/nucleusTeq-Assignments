package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for submitting feedback by panel
 */
public class FeedbackRequestDTO {

    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    @NotNull(message = "Panel ID is required")
    private Long panelId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Comments are required")
    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;

    @NotBlank(message = "Strengths are required")
    @Size(max = 1000)
    private String strengths;

    @NotBlank(message = "Weaknesses are required")
    @Size(max = 1000)
    private String weaknesses;

    @NotBlank(message = "Areas covered are required")
    @Size(max = 1000)
    private String areasCovered;

    @NotBlank(message = "Status is required")
    private String status;

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getAreasCovered() {
        return areasCovered;
    }

    public void setAreasCovered(String areasCovered) {
        this.areasCovered = areasCovered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}