package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import java.time.LocalDateTime;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationSource;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

/**
 * DTO used for sending response to client
 */
public class CandidateResponseDTO {

    private Long id;

    private String mobileNumber;
    private String resumeUrl;
    private String currentCompany;

    private Integer totalExperience;
    private Integer relevantExperience;

    private Double currentCTC;
    private Double expectedCTC;

    private Integer noticePeriod;
    private String preferredLocation;

    private ApplicationSource source;
    private InterviewStage currentStage;
    private ApplicationStatus applicationStatus;

    private Long userId;
    private Long jobId;
    private LocalDateTime createdAt;

    public CandidateResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    }
}