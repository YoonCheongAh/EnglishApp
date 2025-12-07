package com.example.englishapp.api;
import com.example.englishapp.model.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ProfileService {
    @GET("/api/auth/profile")
    Call<UserProfileResponse> getProfile(@Header("Authorization") String authHeader);
}
