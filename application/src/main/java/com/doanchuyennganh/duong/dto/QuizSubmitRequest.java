package com.doanchuyennganh.duong.dto;

import java.util.Map;

public class QuizSubmitRequest {
    private Integer userId;
    private Map<Integer, String> answers; // dailyWordId -> user's answer

    public Integer getUserId() { return userId; }
    public void setUserId(Integer id) { this.userId = id; }

    public Map<Integer, String> getAnswers() { return answers; }
    public void setAnswers(Map<Integer, String> ans) { this.answers = ans; }
}
