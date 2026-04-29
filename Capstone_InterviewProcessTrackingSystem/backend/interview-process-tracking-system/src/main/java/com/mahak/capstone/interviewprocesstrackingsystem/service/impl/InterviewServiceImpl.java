package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
            InterviewMapper interviewMapper,
            InterviewPanelAssignmentMapper assignmentMapper,
            InterviewValidation interviewValidation,
            EmailService emailService) {

        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.jdRepository = jdRepository;
        this.panelRepository = panelRepository;
        this.assignmentRepository = assignmentRepository;
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

        interviewValidation.validateInterviewRequest(dto);

        CandidateProfile candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> {
                    logger.error("Candidate not found: {}", dto.getCandidateId());
                    return new ResourceNotFoundException(ErrorConstants.CANDIDATE_NOT_FOUND);
                });

        JobDescription jd = jdRepository.findById(dto.getJobDescriptionId())
                .orElseThrow(() -> {
                    logger.error("JD not found: {}", dto.getJobDescriptionId());
                    return new ResourceNotFoundException(ErrorConstants.JD_NOT_FOUND);
                });

        Interview interview = interviewMapper.toEntity(dto, candidate, jd);

        interview = interviewRepository.save(interview);

        logger.info("Interview scheduled successfully with id: {}", interview.getId());

        // Send email notification to candidate
        emailService.sendInterviewScheduleToCandidate(
                candidate.getUser().getEmail(),
                candidate.getUser().getFullName(),
                jd.getTitle(),
                interview.getStage().name(),
                interview.getInterviewDateTime().format(DATE_FMT),
                interview.getFocusArea()
        );

        return interviewMapper.toResponseDTO(interview);
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
    }

    /**
     * Get interview by ID
     */
    @Override
    public InterviewResponseDTO getInterviewById(Long id) {

        logger.info("Fetching interview with id: {}", id);

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.INTERVIEW_NOT_FOUND));

        return interviewMapper.toResponseDTO(interview);
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
            .toList();
    }

    /**
     * Progress candidate to next stage after interview.
     */

    @Override
    public CandidateResponseDTO progressCandidateStage(StageProgressionRequestDTO dto) {
        logger.info("Progressing stage for candidateId: {} to new stage: {}", dto.getCandidateId(), dto.getNewStage());
        
        CandidateProfile candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CANDIDATE_NOT_FOUND));

        if ("SELECTED".equalsIgnoreCase(dto.getNewStage())) {
            candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.SELECTED);
        } else if ("REJECTED".equalsIgnoreCase(dto.getNewStage())) {
            candidate.setApplicationStatus(com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus.REJECTED);
        } else {
            try {
                com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage newStage = 
                    com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage.valueOf(dto.getNewStage().toUpperCase());
                candidate.setCurrentStage(newStage);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid stage: " + dto.getNewStage());
            }
        }
        
        candidate = candidateRepository.save(candidate);
        
        return com.mahak.capstone.interviewprocesstrackingsystem.mapper.CandidateMapper.toDTO(candidate);
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