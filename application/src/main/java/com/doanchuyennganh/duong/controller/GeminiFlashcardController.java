package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.model.Flashcard;
import com.doanchuyennganh.duong.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/gemini-flashcards")
@Tag(name = "Gemini Flashcard Generation")
public class GeminiFlashcardController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/status")
    @Operation(summary = "Check API configuration status")
    public ResponseEntity<?> checkStatus() {

        Map<String, Object> result = new HashMap<>();
        Map<String, String> status = geminiService.getApiStatus();

        result.put("success", true);
        result.put("apis", status);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/generate-from-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateFromImage(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(error("File is required"));
        }

        if (!geminiService.isGeminiConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error("Gemini API key not configured"));
        }

        try {
            List<Flashcard> cards = geminiService.generateFlashcardsFromImage(file);

            return ResponseEntity.ok(success(cards));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(error(e.getMessage()));
        }
    }

    @PostMapping("/generate-from-words")
    public ResponseEntity<?> generateFromWords(@RequestBody Map<String, List<String>> body) {

        List<String> words = body.get("words");

        if (words == null || words.isEmpty()) {
            return ResponseEntity.badRequest().body(error("Words list is required"));
        }

        if (!geminiService.isGeminiConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error("Gemini API key not configured"));
        }

        try {
            List<Flashcard> cards = geminiService.generateFlashcardsFromWords(words);
            return ResponseEntity.ok(success(cards));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(error(e.getMessage()));
        }
    }

    private Map<String, Object> success(List<Flashcard> cards) {
        return Map.of(
                "success", true,
                "total", cards.size(),
                "flashcards", cards
        );
    }

    private Map<String, Object> error(String message) {
        return Map.of(
                "success", false,
                "error", message
        );
    }
}
