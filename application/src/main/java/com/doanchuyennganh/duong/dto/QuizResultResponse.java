package com.doanchuyennganh.duong.dto;

import java.util.List;

public class QuizResultResponse {
    private int correctCount;
    private int totalQuestions;
    private double score; // Phần trăm
    private List<QuestionResult> results;

    public static class QuestionResult {
        private Integer dailyWordId;
        private String word;
        private String userAnswer;
        private String correctAnswer;
        private boolean isCorrect;

        // Getters and Setters
        public Integer getDailyWordId() { return dailyWordId; }
        public void setDailyWordId(Integer id) { this.dailyWordId = id; }

        public String getWord() { return word; }
        public void setWord(String word) { this.word = word; }

        public String getUserAnswer() { return userAnswer; }
        public void setUserAnswer(String ans) { this.userAnswer = ans; }

        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String ans) { this.correctAnswer = ans; }

        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }

    // Getters and Setters
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int count) { this.correctCount = count; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int total) { this.totalQuestions = total; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public List<QuestionResult> getResults() { return results; }
    public void setResults(List<QuestionResult> r) { this.results = r; }
}
