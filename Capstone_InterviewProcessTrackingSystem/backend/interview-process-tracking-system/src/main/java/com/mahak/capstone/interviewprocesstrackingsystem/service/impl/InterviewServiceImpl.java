package com.mahak.capstone.interviewprocesstrackingsystem.service.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.InterviewResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelAssignmentRequestDTO;
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
import com.mahak.capstone.interviewprocesstrackingsystem.service.InterviewService;
import com.mahak.capstone.interviewprocesstrackingsystem.validation.InterviewValidation;

/**
 * Implementation of InterviewService
 */
@Service
public class InterviewServiceImpl implements InterviewService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewServiceImpl.class);

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final JobDescriptionRepository jdRepository;
    private final PanelProfileRepository panelRepository;
    private final InterviewPanelAssignmentRepository assignmentRepository;

    private final InterviewMapper interviewMapper;
    private final InterviewPanelAssignmentMapper assignmentMapper;
    private final InterviewValidation interviewValidation;

    public InterviewServiceImpl(
            InterviewRepository interviewRepository,
            CandidateRepository candidateRepository,
            JobDescriptionRepository jdRepository,
            PanelProfileRepository panelRepository,
            InterviewPanelAssignmentRepository assignmentRepository,
            InterviewMapper interviewMapper,
            InterviewPanelAssignmentMapper assignmentMapper,
            InterviewValidation interviewValidation) {

        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.jdRepository = jdRepository;
        this.panelRepository = panelRepository;
        this.assignmentRepository = assignmentRepository;
        this.interviewMapper = interviewMapper;
        this.assignmentMapper = assignmentMapper;
        this.interviewValidation = interviewValidation;
    }

    /**
     * Schedule interview
     */
    @Override
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

        return interviewMapper.toResponseDTO(interview);
    }

    /**
     * Assign panel to interview
     */
    @Override
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
}