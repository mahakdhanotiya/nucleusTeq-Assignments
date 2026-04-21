package com.mahak.capstone.interviewprocesstrackingsystem.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.LoginRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.RegisterRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;
import com.mahak.capstone.interviewprocesstrackingsystem.enums.Role;
import com.mahak.capstone.interviewprocesstrackingsystem.repository.UserRepository;
import com.mahak.capstone.interviewprocesstrackingsystem.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // constructor injection
    public AuthService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   JwtUtil jwtUtil) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
}
    
    /**
     * Register new user
     */
    public void register(RegisterRequestDTO dto) {
        
        // check duplicate email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // create user entity
        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim());

        //  encrypt password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // assign default role
        user.setRole(Role.CANDIDATE);

        // save to DB
        userRepository.save(user);
    }


    //  Login method
    public String login(LoginRequestDTO dto) {

        // user fetch
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // password check
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        //  generate JWT token
        return jwtUtil.generateToken(user.getEmail());
    }

}
