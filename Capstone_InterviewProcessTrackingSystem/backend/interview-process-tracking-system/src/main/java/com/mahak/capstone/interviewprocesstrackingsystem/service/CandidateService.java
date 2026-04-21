package com.mahak.capstone.interviewprocesstrackingsystem.service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.CandidateResponseDTO;

public interface CandidateService {

    //create candidate
    CandidateResponseDTO createCandidate(CandidateRequestDTO candidate);
}