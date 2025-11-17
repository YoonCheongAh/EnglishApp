package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.*;
import com.doanchuyennganh.duong.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz API", description = "API cho chức năng quiz ôn tập từ vựng")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * POST /api/quiz/generate
     * Tạo quiz từ các từ đã học
     */
    @PostMapping("/generate")
    @Operation(summary = "Tạo quiz từ các từ đã học")
    public ResponseEntity<QuizResponse> generateQuiz(@RequestBody QuizGenerateRequest request) {

        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Default 10 questions nếu không specify
        int numberOfQuestions = request.getNumberOfQuestions() != null ?
                request.getNumberOfQuestions() : 10;

        QuizResponse response = quizService.generateQuiz(request.getUserId(), numberOfQuestions);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/quiz/submit
     * Submit quiz và nhận kết quả
     */
    @PostMapping("/submit")
    @Operation(summary = "Submit quiz và nhận kết quả chấm điểm")
    public ResponseEntity<QuizResultResponse> submitQuiz(@RequestBody QuizSubmitRequest request) {

        if (request.getUserId() == null || request.getAnswers() == null) {
            return ResponseEntity.badRequest().build();
        }

        QuizResultResponse response = quizService.submitQuiz(
                request.getUserId(),
                request.getAnswers()
        );

        return ResponseEntity.ok(response);
    }
}
