package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.InterviewPanelAssignment;

public interface InterviewPanelAssignmentRepository 
        extends JpaRepository<InterviewPanelAssignment, Long> {

    // Get all panel assignments for an interview
    List<InterviewPanelAssignment> findByInterviewId(Long interviewId);

}