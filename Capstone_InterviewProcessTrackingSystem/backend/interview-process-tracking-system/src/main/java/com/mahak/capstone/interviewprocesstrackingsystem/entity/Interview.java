package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import java.time.LocalDateTime;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus;

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

/**
 * Represents an Interview scheduled for a candidate.
 *
 * Features:
 * - Supports multiple panels (handled via separate entity)
 * - Tracks stage, status, and focus area
 * - Linked with candidate and job description
 */
@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date and time of the interview
     */
    @Column(nullable = false)
    private LocalDateTime interviewDateTime;

    /**
     * Stage of interview (L1, L2, HR, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStage stage;

    /**
     * Status of interview (SCHEDULED, COMPLETED, CANCELLED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    /**
     * Focus areas for panel (skills/topics to evaluate)
     */
    @Column(nullable = false, length = 500)
    private String focusArea;

    /**
     * Candidate associated with this interview
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    /**
     * Job Description associated (optional but recommended)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jd_id")
    private JobDescription jobDescription;

    /**
     * Auto timestamp when record is created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Auto timestamp when record is updated
     */
    private LocalDateTime updatedAt;

    
    // Lifecycle Hooks


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    // Constructors


    public Interview() {}

    
    // Getters & Setters


    public Long getId() {
        return id;
    }

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public InterviewStage getStage() {
        return stage;
    }

    public void setStage(InterviewStage stage) {
        this.stage = stage;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public CandidateProfile getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateProfile candidate) {
        this.candidate = candidate;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}