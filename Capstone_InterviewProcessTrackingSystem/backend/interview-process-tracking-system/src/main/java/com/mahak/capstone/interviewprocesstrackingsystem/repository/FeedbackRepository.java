package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Get all feedback for an interview
    List<Feedback> findByInterviewId(Long interviewId);

    // Check if feedback already exists (important rule)
    Optional<Feedback> findByInterviewIdAndPanelId(Long interviewId, Long panelId);

}