package com.mahak.capstone.interviewprocesstrackingsystem.dto;

/**
 * DTO for returning feedback details
 */
public class FeedbackResponseDTO {

    private Long id;
    private Long panelId;
    private String panelName;
    private Long interviewId;
    private String interviewStage;
    private Long candidateId;
    private String candidateName;
    private Integer rating;
    private String status;
    private String comments;
    private String strengths;
    private String weaknesses;
    private String areasCovered;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPanelId() { return panelId; }
    public void setPanelId(Long panelId) { this.panelId = panelId; }

    public String getPanelName() { return panelName; }
    public void setPanelName(String panelName) { this.panelName = panelName; }

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

    public String getInterviewStage() { return interviewStage; }
    public void setInterviewStage(String interviewStage) { this.interviewStage = interviewStage; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getAreasCovered() { return areasCovered; }
    public void setAreasCovered(String areasCovered) { this.areasCovered = areasCovered; }
}