package com.doanchuyennganh.duong.dto;

public class QuizGenerateRequest {
    private Integer userId;
    private Integer numberOfQuestions; // Số câu hỏi (default: 10)

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }
    public void setNumberOfQuestions(Integer n) { this.numberOfQuestions = n; }
}
