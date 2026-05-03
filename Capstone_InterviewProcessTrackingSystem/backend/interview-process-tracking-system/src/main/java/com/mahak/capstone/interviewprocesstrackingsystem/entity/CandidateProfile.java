package com.mahak.capstone.interviewprocesstrackingsystem.entity;

import java.time.LocalDateTime;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationSource;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Entity representing Candidate Profile.
 * Stores candidate details and job application information.
 *
 * Business Rule:
 * - One user can have only ONE active application at a time
 *   (enforced in service layer, not database).
 */


@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Candidate mobile number
     */

    @Column(nullable = false, length = 10)
    private String mobileNumber;


    @Column(nullable = false)
    private String resumeUrl;

    private String currentCompany;

    /**
     * Total experience in years
     */

    @Column(nullable = false)
    private Integer totalExperience;

    /**
     * Timestamp when profile is created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Relevant experience for the job
     */
    private Integer relevantExperience;

    private Double currentCTC;
    private Double expectedCTC;

    private Integer noticePeriod;

    private String preferredLocation;

    /**
     * Source of application (Referral, LinkedIn, etc.)
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationSource source;

    /**
     * Current interview stage
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStage currentStage;

    /**
     * Application status
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;


    /**
     * Candidate user (Many applications possible over time)
     */

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Job applied for
     */
    @ManyToOne(optional=false)
    @JoinColumn(name = "job_id", nullable = false)
    private JobDescription jobDescription;

    // Default constructor
    public CandidateProfile() {}

    /**
     * Automatically sets default values before saving
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.currentStage = InterviewStage.PROFILING;
        this.applicationStatus = ApplicationStatus.PROFILING_COMPLETED;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public Integer getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(Integer totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Integer getRelevantExperience() {
        return relevantExperience;
    }

    public void setRelevantExperience(Integer relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    public Double getCurrentCTC() {
        return currentCTC;
    }

    public void setCurrentCTC(Double currentCTC) {
        this.currentCTC = currentCTC;
    }

    public Double getExpectedCTC() {
        return expectedCTC;
    }

    public void setExpectedCTC(Double expectedCTC) {
        this.expectedCTC = expectedCTC;
    }

    public Integer getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(Integer noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public ApplicationSource getSource() {
        return source;
    }

    public void setSource(ApplicationSource source) {
        this.source = source;
    }


    public InterviewStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(InterviewStage currentStage) {
        this.currentStage = currentStage;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }
}


    