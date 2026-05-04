package com.mahak.capstone.interviewprocesstrackingsystem.dto;

import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationSource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO used for incoming requests (Create/Update Candidate)
 */
public class CandidateRequestDTO {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    /**
     * Resume URL (Google Drive link, only PDF allowed)
     */
    @NotBlank(message = "Resume URL is required")
    @Pattern(regexp = ".*\\.pdf$", message = "Resume must be a PDF file")
    private String resumeUrl;

    private String currentCompany;

    @NotNull(message = "Total experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer totalExperience;

    @Min(value = 0, message = "Relevant experience cannot be negative")
    private Integer relevantExperience;

    private Double currentCTC;
    private Double expectedCTC;

    @Min(value = 0, message = "Notice period cannot be negative")
    private Integer noticePeriod;

    private String preferredLocation;

    
    @NotNull(message = "Application source is required")
    private ApplicationSource source;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Job ID is required")
    private Long jobId;

    // Default Constructor
    public CandidateRequestDTO() {}

    //Getters and Setters

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
}