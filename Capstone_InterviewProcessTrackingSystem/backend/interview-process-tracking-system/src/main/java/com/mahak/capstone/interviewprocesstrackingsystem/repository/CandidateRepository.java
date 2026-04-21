package com.mahak.capstone.interviewprocesstrackingsystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateProfile, Long> {
}
