package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "interview_panel_assignments",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"interview_id", "panel_id"})
    }
)
public class InterviewPanelAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Interview
    @ManyToOne(optional = false)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    //  Panel (User with PANEL role)
    @ManyToOne(optional = false)
    @JoinColumn(name = "panel_id", nullable = false)
    private User panel;

    // Focus area for evaluation
    @Column(nullable = false)
    private String focusArea;

    // Default constructor
    public InterviewPanelAssignment() {}

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public User getPanel() {
        return panel;
    }

    public void setPanel(User panel) {
        this.panel = panel;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }
}