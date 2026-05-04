package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;

public interface PanelProfileRepository extends JpaRepository<PanelProfile, Long> {

    // Find panel by user id
    Optional<PanelProfile> findByUserId(Long userId);
    Optional<PanelProfile> findByUserEmail(String email);
    boolean existsByUserId(Long userId);
    boolean existsByMobileNumber(String mobileNumber);

}