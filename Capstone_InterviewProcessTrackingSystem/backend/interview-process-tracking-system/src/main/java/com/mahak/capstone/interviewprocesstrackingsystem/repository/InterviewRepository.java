package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.Interview;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    // Get all interviews of a candidate
    List<Interview> findByCandidateId(Long candidateId);

}