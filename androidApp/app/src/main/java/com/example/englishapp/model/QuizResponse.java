package com.example.englishapp.model;

import java.util.List;

public class QuizResponse {
    private List<QuizQuestion> questions;
    private int totalQuestions;
    private String message;

    public List<QuizQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestion> q) { this.questions = q; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int total) { this.totalQuestions = total; }

    public String getMessage() { return message; }
    public void setMessage(String msg) { this.message = msg; }
}
