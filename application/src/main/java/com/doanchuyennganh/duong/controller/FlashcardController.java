package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.FlashcardRequest;
import com.doanchuyennganh.duong.dto.FlashcardResponse;
import com.doanchuyennganh.duong.model.Flashcard;
import com.doanchuyennganh.duong.service.FlashcardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
@Tag(name = "Flashcard API")
public class FlashcardController {
    private final FlashcardService service;

    public FlashcardController(FlashcardService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả flashcards")
    public List<FlashcardResponse> getAllFlashcards() {
        return service.getAllFlashcards();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy flashcard theo ID")
    public FlashcardResponse getFlashcardById(@PathVariable Long id) {
        return service.getFlashcardById(id);
    }

    @PostMapping
    @Operation(summary = "Tạo flashcard mới")
    public FlashcardResponse createFlashcard(@RequestBody FlashcardRequest request) {
        return service.createFlashcard(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật flashcard theo ID")
    public FlashcardResponse updateFlashcard(@PathVariable Long id, @RequestBody FlashcardRequest request) {
        return service.updateFlashcard(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa flashcard")
    public void deleteFlashcard(@PathVariable Long id) {
        service.deleteFlashcard(id);
    }
}
