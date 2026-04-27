/*package com.mahak.capstone.interviewprocesstrackingsystem.repository;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mahak.capstone.interviewprocesstrackingsystem.entity.CandidateProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.JobDescription;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationSource;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.ApplicationStatus;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.InterviewStage;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CandidateRepositoryTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobDescriptionRepository jobRepository;


    @Test
    void testSaveCandidateProfile() {

        // Step 1: Create and save User (required for FK)
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@gmail.com");
        user.setPassword("123");
        user.setRole(Role.CANDIDATE);

        user = userRepository.save(user); 

        // Step 2: Create and save JobDescription (required for FK)
        JobDescription job = new JobDescription();
        job.setTitle("Java Developer");
        job.setDescription("Backend role");
        job.setRequiredSkills("Java, Spring");
        job.setMinExperience(1);
        job.setMaxExperience(3);
        job.setLocation("Remote");

        job = jobRepository.save(job); // persist job

        // Step 3: Create CandidateProfile entity
        CandidateProfile candidate = new CandidateProfile();
        candidate.setMobileNumber("9" + System.currentTimeMillis());
        candidate.setResumePath("resume.pdf");
        candidate.setTotalExperience(2);
        candidate.setRelevantExperience(1);

        // enums 
        candidate.setSource(ApplicationSource.LINKEDIN);
        candidate.setCurrentStage(InterviewStage.PROFILING);
        candidate.setApplicationStatus(ApplicationStatus.PROFILING_COMPLETED);

        // relationships 
        candidate.setUser(user);
        candidate.setJobDescription(job);

        // Step 4: Save CandidateProfile
        CandidateProfile savedCandidate = candidateRepository.save(candidate);

        // Step 5: Assertions (validation)
        assertNotNull(savedCandidate.getId(), "Candidate ID should not be null after save");
        assertEquals(candidate.getMobileNumber(), savedCandidate.getMobileNumber());
        assertEquals(ApplicationStatus.PROFILING_COMPLETED, savedCandidate.getApplicationStatus());
    }
}*/