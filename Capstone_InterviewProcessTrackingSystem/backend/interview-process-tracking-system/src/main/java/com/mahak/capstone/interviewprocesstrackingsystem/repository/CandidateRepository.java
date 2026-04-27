package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;


@Repository
public interface CandidateRepository extends JpaRepository<CandidateProfile, Long> {
    /**
     * Check if user already has an active (non-rejected) application
     */
    boolean existsByUserAndApplicationStatusNot(User user, ApplicationStatus status);

    Optional<CandidateProfile> findByUser(User user);

    /**
     * Filter candidates by JD, stage, and/or status.
     */
    @Query("SELECT c FROM CandidateProfile c WHERE "
         + "(:jdId IS NULL OR c.jobDescription.id = :jdId) AND "
         + "(:stage IS NULL OR c.currentStage = :stage) AND "
         + "(:status IS NULL OR c.applicationStatus = :status)")
    List<CandidateProfile> findByFilters(
            @Param("jdId") Long jdId,
            @Param("stage") InterviewStage stage,
            @Param("status") ApplicationStatus status
    );
}
