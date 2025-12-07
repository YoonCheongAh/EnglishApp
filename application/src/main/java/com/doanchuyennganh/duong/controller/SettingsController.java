package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.SettingsDTO;
import com.doanchuyennganh.duong.model.UserSettings;
import com.doanchuyennganh.duong.service.UserService;
import com.doanchuyennganh.duong.service.UserSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "User Settings API")
public class SettingsController {
    private final UserSettingsService settingsService;
    private final UserService userService;

    public SettingsController(UserSettingsService settingsService, UserService userService) {
        this.settingsService = settingsService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Lấy settings của user hiện tại")
    public ResponseEntity<UserSettings> getSettings(@Parameter(hidden = true) Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);
        if(userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(settingsService.getUserSettings(userId));
    }

    @PutMapping
    @Operation(summary = "Cập nhật toàn bộ settings")
    public ResponseEntity<UserSettings> updateSettings(
            @RequestBody SettingsDTO settingsDTO,
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if(userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(settingsService.updateSettings(userId, settingsDTO));
    }

    @PatchMapping("/notification")
    @Operation(summary = "Cập nhật notification setting")
    public ResponseEntity<UserSettings> updateNotification(
            @RequestParam Boolean enabled,
            @Parameter(hidden = true) Authentication authentication) {
        return updateSingleSetting(authentication, dto -> dto.setNotificationEnabled(enabled));
    }

    @PatchMapping("/daily-goal")
    @Operation(summary = "Cập nhật daily goal")
    public ResponseEntity<UserSettings> updateDailyGoal(
            @RequestParam Integer goal,
            @Parameter(hidden = true) Authentication authentication) {
        return updateSingleSetting(authentication, dto -> dto.setDailyGoal(goal));
    }

    @PatchMapping("/font-size")
    @Operation(summary = "Cập nhật font size")
    public ResponseEntity<UserSettings> updateFontSize(
            @RequestParam Integer fontSize,
            @Parameter(hidden = true) Authentication authentication) {
        return updateSingleSetting(authentication, dto -> dto.setFontSize(fontSize));
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset settings về mặc định")
    public ResponseEntity<UserSettings> resetSettings(@Parameter(hidden = true) Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);
        if(userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(settingsService.resetSettings(userId));
    }

    @GetMapping("/daily-goal")
    @Operation(summary = "Lấy daily goal")
    public ResponseEntity<Integer> getDailyGoal(@Parameter(hidden = true) Authentication authentication) {
        Integer userId = getUserIdFromAuth(authentication);
        if(userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(settingsService.getDailyGoal(userId));
    }

    /** Helper method để tránh lặp code cho các PATCH nhỏ*/
    private ResponseEntity<UserSettings> updateSingleSetting(Authentication authentication, java.util.function.Consumer<SettingsDTO> updater) {
        Integer userId = getUserIdFromAuth(authentication);
        if(userId == null) return ResponseEntity.status(401).build();

        SettingsDTO dto = new SettingsDTO();
        updater.accept(dto);
        return ResponseEntity.ok(settingsService.updateSettings(userId, dto));
    }

    private Integer getUserIdFromAuth(Authentication authentication) {
        if(authentication == null || authentication.getName() == null) return null;
        Long id = userService.getUserIdFromUsername(authentication.getName());
        return id != null ? id.intValue() : null;
    }
}
