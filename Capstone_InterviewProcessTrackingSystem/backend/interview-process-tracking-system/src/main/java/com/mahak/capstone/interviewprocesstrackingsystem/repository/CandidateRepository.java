package com.mahak.capstone.interviewprocesstrackingsystem.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;


@Repository
public interface CandidateRepository extends JpaRepository<CandidateProfile, Long> {
    /**
     * Check if user already has an active (non-rejected) application
     */
    boolean existsByUserAndApplicationStatusNot(User user, ApplicationStatus status);
    Optional<CandidateProfile> findByUser(User user);
}

