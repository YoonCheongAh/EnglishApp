package com.example.englishapp.api;
import com.example.englishapp.model.AuthResponse;
import com.example.englishapp.model.LoginRequest;
import com.example.englishapp.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
}