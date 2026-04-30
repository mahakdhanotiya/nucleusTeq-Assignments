package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import org.springframework.transaction.annotation.Transactional;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.StageProgressionRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.InterviewPanelAssignment;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.ResourceNotFoundException;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.InterviewMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.mapper.InterviewPanelAssignmentMapper;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.CandidateRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewPanelAssignmentRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.InterviewRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.JobDescriptionRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.PanelProfileRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.FeedbackRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.service.EmailService;
import com.mahak.capstone.interviewprocesstrackingsystem.service.InterviewService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.InterviewValidation;

/**
 * Implementation of InterviewService
 */
@Service
public class InterviewServiceImpl implements InterviewService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewServiceImpl.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final JobDescriptionRepository jdRepository;
    private final PanelProfileRepository panelRepository;
    private final InterviewPanelAssignmentRepository assignmentRepository;
    private final FeedbackRepository feedbackRepository;

    private final InterviewMapper interviewMapper;
    private final InterviewPanelAssignmentMapper assignmentMapper;
    private final InterviewValidation interviewValidation;
    private final EmailService emailService;

    public InterviewServiceImpl(
            InterviewRepository interviewRepository,
            CandidateRepository candidateRepository,
            JobDescriptionRepository jdRepository,
            PanelProfileRepository panelRepository,
            InterviewPanelAssignmentRepository assignmentRepository,
            FeedbackRepository feedbackRepository,
            InterviewMapper interviewMapper,
            InterviewPanelAssignmentMapper assignmentMapper,
            InterviewValidation interviewValidation,
            EmailService emailService) {

        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.jdRepository = jdRepository;
        this.panelRepository = panelRepository;
        this.assignmentRepository = assignmentRepository;
        this.feedbackRepository = feedbackRepository;
        this.interviewMapper = interviewMapper;
        this.assignmentMapper = assignmentMapper;
        this.interviewValidation = interviewValidation;
        this.emailService = emailService;
    }

    /**
     * Schedule interview
     */
    @Override
    @Transactional
    public InterviewResponseDTO scheduleInterview(InterviewRequestDTO dto) {

        logger.info("Scheduling interview for candidateId: {}", dto.getCandidateId());

        CandidateProfile candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> {
                    logger.error("Candidate not found: {}", dto.getCandidateId());
                    return new ResourceNotFoundException(ErrorConstants.CANDIDATE_NOT_FOUND);
                });

        // VALIDATION: Ensure the requested stage matches the candidate's current stage
        interviewValidation.validateInterviewRequest(dto, candidate.getCurrentStage());

        // VALIDATION: Ensure previous stage interview is COMPLETED before scheduling next one
        com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage requestedStage = 
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.valueOf(dto.getStage().toUpperCase());
        com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage previousStage = getPreviousStage(requestedStage);
        
        if (previousStage != null && candidate.getCurrentStage().ordinal() < requestedStage.ordinal()) {
            List<Interview> previousInterviews = interviewRepository.findByCandidateIdAndStage(
                dto.getCandidateId(), previousStage);
            boolean previousCompleted = previousInterviews.stream()
                .anyMatch(i -> i.getStatus() == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus.COMPLETED);
            if (!previousCompleted) {
                throw new InvalidRequestException(
                    "Cannot schedule " + requestedStage + " interview. The " + previousStage + " interview must be completed first.");
            }
        }

        JobDescription jd = jdRepository.findById(dto.getJobDescriptionId())
                .orElseThrow(() -> {
                    logger.error("JD not found: {}", dto.getJobDescriptionId());
                    return new ResourceNotFoundException(ErrorConstants.JD_NOT_FOUND);
                });

        Interview interview = interviewMapper.toEntity(dto, candidate, jd);
        
        // SYNC: Automatically update candidate's current stage and status to match the scheduled interview round
        candidate.setCurrentStage(interview.getStage());
        candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.INTERVIEW_SCHEDULED);
        candidateRepository.save(candidate);

        interview = interviewRepository.save(interview);

        logger.info("Interview scheduled successfully with id: {}. Candidate stage synced to: {}", interview.getId(), interview.getStage());

        // Send email notification to candidate
        emailService.sendInterviewScheduleToCandidate(
                candidate.getUser().getEmail(),
                candidate.getUser().getFullName(),
                jd.getTitle(),
                interview.getStage().name(),
                interview.getInterviewDateTime().format(DATE_FMT),
                interview.getFocusArea()
        );

        return enrichInterviewDTO(interviewMapper.toResponseDTO(interview));
    }

    private InterviewResponseDTO enrichInterviewDTO(InterviewResponseDTO dto) {
        // Fetch the actual interview entity to get candidate details easily
        Interview interview = interviewRepository.findById(dto.getId()).orElse(null);
        if (interview != null && interview.getCandidate() != null) {
            dto.setCandidateId(interview.getCandidate().getId());
            dto.setCandidateCurrentStage(interview.getCandidate().getCurrentStage().name());
            dto.setCandidateCurrentStatus(interview.getCandidate().getApplicationStatus().name());
            dto.setCandidateResumeUrl(interview.getCandidate().getResumeUrl());
        }

        // Fetch assignments
        List<InterviewPanelAssignment> assignments = assignmentRepository.findByInterviewId(dto.getId());
        dto.setAssignedPanelNames(assignments.stream().map(a -> a.getPanel().getUser().getFullName()).toList());
        dto.setAssignedPanelIds(assignments.stream().map(a -> a.getPanel().getId()).toList());
        dto.setAssignedPanelFocusAreas(assignments.stream().map(a -> a.getFocusArea()).toList());

        // Fetch feedback providers
        List<com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback> feedbackList = 
            feedbackRepository.findByInterviewId(dto.getId());
        dto.setFeedbackProvidedBy(feedbackList.stream().map(f -> f.getPanel() != null ? f.getPanel().getId() : null).toList());
        
        return dto;
    }

    /**
     * Assign panel to interview
     */
    @Override
    @Transactional
    public void assignPanel(PanelAssignmentRequestDTO dto) {

        logger.info("Assigning panel {} to interview {}", dto.getPanelId(), dto.getInterviewId());

        interviewValidation.validatePanelAssignment(dto);

        Interview interview = interviewRepository.findById(dto.getInterviewId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));

        PanelProfile panel = panelRepository.findById(dto.getPanelId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.PANEL_NOT_FOUND));

        List<InterviewPanelAssignment> list =
                assignmentRepository.findByInterviewId(dto.getInterviewId());

        // duplicate check
        boolean alreadyAssigned = list.stream()
                .anyMatch(a -> Objects.equals(a.getPanel().getId(), dto.getPanelId()));

        if (alreadyAssigned) {
            logger.error("Panel already assigned to interviewId: {}", dto.getInterviewId());
            throw new InvalidRequestException(ErrorConstants.PANEL_ALREADY_ASSIGNED);
        }

        // max 2 rule
        if (list.size() >= 2) {
            logger.error("Panel limit exceeded for interviewId: {}", dto.getInterviewId());
            throw new InvalidRequestException(ErrorConstants.PANEL_LIMIT_EXCEEDED);
        }

        InterviewPanelAssignment assignment =
                assignmentMapper.toEntity(dto, interview, panel);

        assignmentRepository.save(assignment);

        logger.info("Panel assigned successfully");

        // Send email notification to the panel interviewer
        emailService.sendPanelAssignmentEmail(
                panel.getUser().getEmail(),
                panel.getUser().getFullName(),
                interview.getCandidate().getUser().getFullName(),
                interview.getJobDescription() != null ? interview.getJobDescription().getTitle() : "N/A",
                interview.getStage().name(),
                interview.getInterviewDateTime().format(DATE_FMT),
                interview.getFocusArea()
        );

        // Notify the Candidate as well
        emailService.sendPanelAssignedToCandidateEmail(
                interview.getCandidate().getUser().getEmail(),
                interview.getCandidate().getUser().getFullName(),
                panel.getUser().getFullName(),
                interview.getStage().name(),
                interview.getInterviewDateTime().format(DATE_FMT)
        );
    }

    /**
     * Get interview by ID
     */
    @Override
    public InterviewResponseDTO getInterviewById(Long id) {

        logger.info("Fetching interview with id: {}", id);

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));

        return enrichInterviewDTO(interviewMapper.toResponseDTO(interview));
    }
    /**
     * Fetching all interviews
     */

    @Override
public List<InterviewResponseDTO> getAllInterviews() {

    logger.info("Fetching all interviews");

    List<Interview> interviews = interviewRepository.findAll();

    if (interviews.isEmpty()) {
        logger.warn("No interviews found");
        throw new ResourceNotFoundException("No interviews found");
    }

    return interviews.stream()
            .map(interviewMapper::toResponseDTO)
            .map(this::enrichInterviewDTO)
            .toList();
    }


     /**
     * get interviews by Candidate.
     */

    @Override
    public List<InterviewResponseDTO> getInterviewsByCandidate(Long candidateId) {

    logger.info("Fetching interviews for candidateId: {}", candidateId);

    List<Interview> interviews = interviewRepository.findByCandidateId(candidateId);

    if (interviews.isEmpty()) {
        logger.warn("No interviews found for candidateId: {}", candidateId);
        throw new ResourceNotFoundException("No interviews found for this candidate");
    }

    return interviews.stream()
            .map(interviewMapper::toResponseDTO)
            .map(this::enrichInterviewDTO)
            .toList();
    }

    /**
     * Progress candidate to next stage after interview.
     * Enforces step-by-step flow: PROFILING -> SCREENING -> L1 -> L2 -> HR
     */
    @Override
    @Transactional
    public CandidateResponseDTO progressCandidateStage(StageProgressionRequestDTO dto) {
        logger.info("Progressing stage for candidateId: {} to new stage: {}", dto.getCandidateId(), dto.getNewStage());
        
        CandidateProfile candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CANDIDATE_NOT_FOUND));

        String newStageStr = dto.getNewStage().toUpperCase();

        // 1. Handling Rejection (Can happen anytime)
        if ("REJECTED".equalsIgnoreCase(newStageStr)) {
            candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.REJECTED);
        } 
        // 2. Handling Selection (Only after HR)
        else if ("SELECTED".equalsIgnoreCase(newStageStr)) {
            if (candidate.getCurrentStage() != com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.HR) {
                throw new InvalidRequestException("Candidate must be in HR stage before being SELECTED");
            }
            candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.SELECTED);
        }
        // 3. Handling Stage Progression
        else {
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage currentStage = candidate.getCurrentStage();
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage nextStage;

            try {
                nextStage = com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.valueOf(newStageStr);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid stage name: " + newStageStr);
            }

            // Enforce Step-by-Step Rule
            validateStageProgression(currentStage, nextStage);

            // GATE: Ensure current stage interview is COMPLETED before moving to next stage
            // (Skip for PROFILING and SCREENING as they are administrative and don't have formal panel rounds)
            boolean isTechnicalRound = currentStage == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.L1 || 
                                     currentStage == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.L2 || 
                                     currentStage == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.HR;

            if (isTechnicalRound && currentStage != nextStage) {
                List<Interview> currentInterviews = interviewRepository.findByCandidateIdAndStage(candidate.getId(), currentStage);
                boolean currentCompleted = currentInterviews.stream()
                    .anyMatch(i -> i.getStatus() == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus.COMPLETED);
                
                if (!currentCompleted) {
                    throw new InvalidRequestException("Cannot progress to " + nextStage + ". The " + currentStage + " interview must be completed and evaluated first.");
                }
            }

            candidate.setCurrentStage(nextStage);
            // Mark as READY (PROFILING_COMPLETED) - will change to INTERVIEW_SCHEDULED once HR schedules it
            candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.PROFILING_COMPLETED);
        }
        
        candidate = candidateRepository.save(candidate);
        logger.info("Candidate {} progressed. New Stage: {}, New Status: {}", 
                candidate.getId(), candidate.getCurrentStage(), candidate.getApplicationStatus());
        
        return com.mahak.capstone.interviewprocesstrackingsystem.mapper.CandidateMapper.toDTO(candidate);
    }

    /**
     * Helper to enforce PROFILING -> SCREENING -> L1 -> L2 -> HR
     */
    private void validateStageProgression(
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage current, 
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage next) {
        
        if (current == next) return; // Allow staying in same stage

        boolean isValid = false;
        switch (current) {
            case PROFILING -> isValid = (next == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.SCREENING);
            case SCREENING -> isValid = (next == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.L1);
            case L1 -> isValid = (next == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.L2);
            case L2 -> isValid = (next == com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.HR);
            case HR -> isValid = false; // Cannot progress past HR (must be SELECTED or REJECTED)
        }

        if (!isValid) {
            throw new InvalidRequestException("Invalid Stage Flow: Cannot move from " + current + " to " + next + ". Must follow step-by-step sequence.");
        }
    }

    /**
     * Helper to get the previous stage in the pipeline.
     * Returns null for PROFILING (first stage, no previous).
     */
    private com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage getPreviousStage(
            com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage stage) {
        return switch (stage) {
            case PROFILING -> null; // First stage, no previous
            case SCREENING -> com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.PROFILING;
            case L1 -> com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.SCREENING;
            case L2 -> com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.L1;
            case HR -> com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.L2;
        };
    }

    /**
     * Update interview details.
     */
    @Override
    @Transactional
    public InterviewResponseDTO updateInterview(Long id, com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewUpdateDTO dto) {
        logger.info("Updating interview with id: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));

        if (dto.getInterviewDateTime() != null) {
            interview.setInterviewDateTime(dto.getInterviewDateTime());
        }
        if (dto.getFocusArea() != null && !dto.getFocusArea().isBlank()) {
            interview.setFocusArea(dto.getFocusArea());
        }

        interview = interviewRepository.save(interview);
        logger.info("Interview updated successfully: {}", id);
        return enrichInterviewDTO(interviewMapper.toResponseDTO(interview));
    }

    /**
     * Delete interview by ID.
     */
    @Override
    @Transactional
    public void deleteInterview(Long id) {
        logger.info("Deleting interview with id: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));
        
        // Delete associated panel assignments first
        List<InterviewPanelAssignment> assignments = assignmentRepository.findByInterviewId(id);
        if (!assignments.isEmpty()) {
            assignmentRepository.deleteAll(assignments);
        }
        
        interviewRepository.delete(interview);
        logger.info("Interview deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public void updateInterviewStatus(Long id, com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStatus status) {
        logger.info("Updating status for interviewId: {} to {}", id, status);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));
        
        interview.setStatus(status);
        interviewRepository.save(interview);
        logger.info("Status updated successfully for interviewId: {}", id);
    }
}