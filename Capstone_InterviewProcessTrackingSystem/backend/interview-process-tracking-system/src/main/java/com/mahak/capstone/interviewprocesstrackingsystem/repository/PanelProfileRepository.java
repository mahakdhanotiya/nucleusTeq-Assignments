package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;

public interface PanelProfileRepository extends JpaRepository<PanelProfile, Long> {

    // Find panel by user id
    Optional<PanelProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

}