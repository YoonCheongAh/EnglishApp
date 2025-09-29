package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.config.JwtUtil;
import com.doanchuyennganh.duong.dto.*;
import com.doanchuyennganh.duong.model.Role;
import com.doanchuyennganh.duong.model.User;
import com.doanchuyennganh.duong.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // Sử dụng LocalDateTime
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    private void validateRegisterRequest(RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    public AuthResponse register(RegisterRequest req) {
        validateRegisterRequest(req);

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFullname(req.getFullname());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now()); // Sử dụng LocalDateTime

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setRole(savedUser.getRole().name());
        return response;
    }

    public AuthResponse login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }

    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setFullname(user.getFullname());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }

    public UserProfileResponse updateProfile(String username, UpdateProfileRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra email trùng lặp nếu có thay đổi email
        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(req.getEmail());
        }

        if (req.getFullname() != null) {
            user.setFullname(req.getFullname());
        }
        if (req.getAvatarUrl() != null) {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now()); // Sử dụng LocalDateTime
        User updatedUser = userRepository.save(user);

        return getProfile(updatedUser.getUsername());
    }

    public String extractUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
}