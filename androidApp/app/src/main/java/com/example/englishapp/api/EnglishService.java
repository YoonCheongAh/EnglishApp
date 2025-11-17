package com.example.englishapp.api;

import com.example.englishapp.model.FlashcardResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EnglishService {
    interface FlashcardApi {
        @GET("api/flashcards/search")
        Call<List<FlashcardResponse>> searchFlashcards(@Query("keyword") String keyword);
    }
}