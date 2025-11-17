package com.example.englishapp.api;

import com.example.englishapp.model.QuizResponse;
import com.example.englishapp.model.QuizResultResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface QuizApi {

    /**
     * Generate quiz
     * POST /api/quiz/generate
     */
    @POST("api/quiz/generate")
    Call<QuizResponse> generateQuiz(@Body Map<String, Object> body);

    /**
     * Submit quiz answers
     * POST /api/quiz/submit
     */
    @POST("api/quiz/submit")
    Call<QuizResultResponse> submitQuiz(@Body Map<String, Object> body);
}