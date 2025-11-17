package com.example.englishapp.model;

public class AuthResponse {
    private String accessToken;
    private Long userId;
    private String username;
    private String role;

    public String getAccessToken() { return accessToken; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
