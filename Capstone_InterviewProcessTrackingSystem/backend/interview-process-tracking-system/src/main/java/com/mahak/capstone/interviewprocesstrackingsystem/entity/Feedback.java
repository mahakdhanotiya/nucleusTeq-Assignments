package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.FeedbackStatus;

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
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String comments;

    @Column(nullable = false)
    private Integer rating; //later validate 1–5 in service layer

    @Column(nullable = false, length = 1000)
    private String strengths;

    @Column(nullable = false, length = 1000)
    private String weaknesses;

    @Column(nullable = false, length = 1000)
    private String areasCovered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    // Interview relation
    @ManyToOne(optional = false)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    // Panel (who gave feedback)
    @ManyToOne(optional = false)
    @JoinColumn(name = "panel_id", nullable = false)
    private User panel;

    // Default constructor
    public Feedback() {}

    // Getters and Setters

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

    public User getPanel() {
        return panel;
    }

    public void setPanel(User panel) {
        this.panel = panel;
    }
}
