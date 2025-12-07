package com.example.englishapp.api;

public class ApiClient {
    public static EnglishService.FlashcardApi getFlashcardApi() {
        return RetrofitClient.getInstance().create(EnglishService.FlashcardApi.class);
    }
}
