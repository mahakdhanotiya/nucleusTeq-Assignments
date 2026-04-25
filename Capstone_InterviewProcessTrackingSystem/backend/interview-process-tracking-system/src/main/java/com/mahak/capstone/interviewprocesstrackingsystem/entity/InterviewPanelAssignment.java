package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Maps Panel members (User) to an Interview.
 * - One interview can have multiple panel members
 * - Same panel cannot be assigned twice to the same interview
 */
@Entity
@Table(
    name = "interview_panel_assignments",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"interview_id", "panel_id"})
    }
)
public class InterviewPanelAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Interview reference
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    /**
     * Panel member (User with PANEL role)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "panel_id", nullable = false)
    private PanelProfile panel;

    /**
     * Focus area for this panel member
     */
    @Column(nullable = false, length = 300)
    private String focusArea;

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

    public PanelProfile getPanel() {
        return panel;
    }

    public void setPanel(PanelProfile panel) {
        this.panel = panel;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}