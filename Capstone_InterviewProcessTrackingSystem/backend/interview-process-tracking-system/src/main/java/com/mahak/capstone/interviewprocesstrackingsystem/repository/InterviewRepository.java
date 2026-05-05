package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    // Get all interviews of a candidate
    List<Interview> findByCandidateId(Long candidateId);

    // Find interviews by candidate and stage (for stage-order validation)
    List<Interview> findByCandidateIdAndStage(Long candidateId, InterviewStage stage);

}