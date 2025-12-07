package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.UserProfileResponse;
import com.doanchuyennganh.duong.model.User;
import com.doanchuyennganh.duong.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping("/avatar")
    public ResponseEntity<UserProfileResponse> updateAvatar(@RequestBody Map<String, String> body) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = profileService.getUserByUsername(username);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Long userId = user.getUserId();
        String base64 = body.get("avatarBase64");

        if (base64 == null || base64.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String url = profileService.saveAvatar(base64, userId);
        profileService.updateAvatarUrl(userId, url);

        User updatedUser = profileService.getProfile(userId);
        UserProfileResponse response = convertToResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    private UserProfileResponse convertToResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullname(user.getFullname());
        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole().name());
        return response;
    }
}