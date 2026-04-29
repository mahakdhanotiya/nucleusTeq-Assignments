package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import java.time.LocalDateTime;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.FeedbackStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents feedback given by a panel member for an interview.
 * 
 * Rules:
 * - One panel can give only one feedback per interview
 * - Rating should be between 1–5 (validated in service layer)
 */
@Entity
@Table(
    name = "feedbacks",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"interview_id", "panel_id"})
    }
)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String comments;

    /**
     * Rating from 1 to 5 (validate in service layer)
     */
    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String strengths;

    @Column(nullable = false, length = 1000)
    private String weaknesses;

    @Column(nullable = false, length = 1000)
    private String areasCovered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    /**
     * Interview reference
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    /**
     * Panel who submitted feedback
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "panel_id", nullable = true)
    private PanelProfile panel;

    /**
     * Audit fields
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // Lifecycle hooks


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructors

    public Feedback() {}


    // Getters & Setters


    public Long getId() {
        return id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
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

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public PanelProfile getPanel() {
        return panel;
    }

    public void setPanel(PanelProfile panel) {
        this.panel = panel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}