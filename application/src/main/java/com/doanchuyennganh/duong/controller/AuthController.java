package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.*;
import com.doanchuyennganh.duong.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User API")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Đăng kí")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy profile")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @Operation(summary = "Sửa profile")
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    private String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return userService.extractUsernameFromToken(token.replace("Bearer ", ""));
        }
        throw new RuntimeException("Invalid token format");
    }

    @Operation(summary = "Đổi mật khẩu")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Token invalid or expired");
        }

        String username = authentication.getName();
        try {
            userService.changePassword(username, request);
            return ResponseEntity.ok("Password updated successfully");
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if (msg.contains("Old password is incorrect")) {
                return ResponseEntity.status(400).body("Mật khẩu hiện tại không đúng");
            } else if (msg.contains("at least 6 characters")) {
                return ResponseEntity.status(400).body("Mật khẩu mới phải ít nhất 6 ký tự");
            } else if (msg.contains("do not match")) {
                return ResponseEntity.status(400).body("Mật khẩu mới và xác nhận không khớp");
            } else {
                return ResponseEntity.status(500).body("Lỗi server: " + msg);
            }
        }
    }
}