package com.example.englishapp.model;

public class CheckinResponse {
    public boolean success;
    public String message;
    private String date; // "2025-11-17"

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
}
