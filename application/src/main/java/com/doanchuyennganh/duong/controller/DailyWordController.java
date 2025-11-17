package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.*;
import com.doanchuyennganh.duong.service.DailyWordService;
import com.doanchuyennganh.duong.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/daily-words")
@Tag(name = "Daily Words API", description = "Các API quản lý từ vựng hàng ngày")
public class DailyWordController {
    private final DailyWordService dailyWordService;
    private final UserService userService;

    public DailyWordController(DailyWordService dailyWordService, UserService userService) {
        this.dailyWordService = dailyWordService;
        this.userService = userService;
    }

    @GetMapping("/today")
    @Operation(summary = "Lấy danh sách từ của ngày hôm nay")
    public ResponseEntity<TodayWordsResponseDTO> getTodayWords(
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        List<DailyWordDTO> words = dailyWordService.getTodayWords(userId);
        long learnedCount = words.stream().filter(DailyWordDTO::getIsLearned).count();

        TodayWordsResponseDTO response = new TodayWordsResponseDTO();
        response.setDate(LocalDate.now().toString());
        response.setTotalWords(words.size());
        response.setLearnedWords((int) learnedCount);
        response.setProgress(words.isEmpty() ? 0 : (int) (learnedCount * 100 / words.size()));
        response.setWords(words);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/learned")
    @Operation(summary = "Lấy tất cả từ đã học của user")
    public ResponseEntity<List<DailyWordDTO>> getAllLearnedWords(
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        List<DailyWordDTO> learnedWords = dailyWordService.getAllLearnedWords(userId);
        return ResponseEntity.ok(learnedWords);
    }

    @PostMapping("/generate")
    @Operation(summary = "Tạo từ mới cho ngày hôm nay (chỉ gọi 1 lần/ngày)")
    public ResponseEntity<GenerateWordsResponseDTO> generateTodayWords(
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        List<DailyWordDTO> words = dailyWordService.generateTodayWords(userId);
        GenerateWordsResponseDTO response = new GenerateWordsResponseDTO();
        response.setMessage("Daily words generated successfully");
        response.setTotalWords(words.size());
        response.setWords(words);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk-learned")
    @Operation(summary = "Đánh dấu nhiều từ cùng lúc")
    public ResponseEntity<Map<String, Object>> markMultipleAsLearned(
            @RequestBody BulkMarkLearnedRequest request,
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        List<DailyWordDTO> updatedWords = dailyWordService.markMultipleAsLearned(
                userId, request.getDailyWordIds(), request.getLearned());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Words updated successfully");
        response.put("updatedCount", updatedWords.size());
        response.put("words", updatedWords);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới từ hôm nay (xóa và tạo lại)")
    public ResponseEntity<Map<String, Object>> refreshTodayWords(
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        List<DailyWordDTO> words = dailyWordService.refreshTodayWords(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Daily words refreshed successfully");
        response.put("totalWords", words.size());
        response.put("words", words);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{dailyWordId}/learned")
    @Operation(summary = "Đánh dấu một từ đã học hoặc chưa học")
    public ResponseEntity<Map<String, Object>> markAsLearned(
            @PathVariable Integer dailyWordId,
            @RequestParam Boolean learned,
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        DailyWordDTO word = dailyWordService.markAsLearned(userId, dailyWordId, learned);
        Map<String, Object> response = new HashMap<>();
        response.put("message", learned ? "Word marked as learned" : "Word marked as not learned");
        response.put("word", word);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Lấy thống kê học tập cho một ngày")
    public ResponseEntity<Object> getStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(hidden = true) Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        if (userId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(dailyWordService.getStatistics(userId, date));
    }

    /* ===== Helper ===== */
    private Integer getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return null;
        try {
            return userService.getUserIdFromUsername(authentication.getName()).intValue();
        } catch (Exception e) {
            return null;
        }
    }
}