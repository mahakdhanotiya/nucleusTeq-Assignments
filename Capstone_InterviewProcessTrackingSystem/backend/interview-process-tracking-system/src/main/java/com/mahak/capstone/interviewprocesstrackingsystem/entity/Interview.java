package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate interviewDate;

    @Column(nullable = false)
    private LocalTime interviewTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStage stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    // Candidate
    @ManyToOne(optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    // Panel (User with PANEL role)
    @ManyToOne
    @JoinColumn(name = "panel_id")
    private User panel;

    // Default constructor
    public Interview() {}

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public LocalTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(LocalTime interviewTime) {
        this.interviewTime = interviewTime;
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

    public CandidateProfile getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateProfile candidate) {
        this.candidate = candidate;
    }

    public User getPanel() {
        return panel;
    }

    public void setPanel(User panel) {
        this.panel = panel;
    }
}