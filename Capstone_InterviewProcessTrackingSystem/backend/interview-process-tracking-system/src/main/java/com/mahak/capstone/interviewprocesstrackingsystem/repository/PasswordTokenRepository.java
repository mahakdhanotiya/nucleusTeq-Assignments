package com.mahak.capstone.interviewprocesstrackingsystem.repository;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByToken(String token);
    Optional<PasswordToken> findByEmailAndUsedFalse(String email);
}
