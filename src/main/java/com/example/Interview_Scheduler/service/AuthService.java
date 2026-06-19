package com.example.Interview_Scheduler.service;

import com.example.Interview_Scheduler.dto.AuthRequest;
import com.example.Interview_Scheduler.dto.AuthResponse;
import com.example.Interview_Scheduler.dto.RegisterRequest;
import com.example.Interview_Scheduler.exception.BusinessException;
import com.example.Interview_Scheduler.model.*;
import com.example.Interview_Scheduler.repository.UserRepository;
import com.example.Interview_Scheduler.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public String register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Registration failed: Email already exists - {}", request.getEmail());
            throw new BusinessException("Email already exists");
        }

        UserModel user = new UserModel();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.INTERVIEWER);

        user = userRepository.save(user);

        log.info("Registration completed successfully for: {}", user.getEmail());
        return "Registration completed successfully";
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", request.getEmail());
            throw new BusinessException("Invalid email or password");
        }

        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());

        log.info("Login successful for email: {}", user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }
}