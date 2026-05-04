package com.mahak.capstone.interviewprocesstrackingsystem.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    List<JobDescription> findByIsActiveTrue();
}