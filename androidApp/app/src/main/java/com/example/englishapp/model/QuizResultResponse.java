package com.example.englishapp.model;
import java.util.List;

public class QuizResultResponse {
    private int correctCount;
    private int totalQuestions;
    private double score;
    private List<QuestionResult> results;

    public static class QuestionResult {
        private Integer dailyWordId;
        private String word;
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;

        public Integer getDailyWordId() { return dailyWordId; }
        public String getWord() { return word; }
        public String getUserAnswer() { return userAnswer; }
        public String getCorrectAnswer() { return correctAnswer; }
        public boolean isCorrect() { return correct; }
    }

    public int getCorrectCount() { return correctCount; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getScore() { return score; }
    public List<QuestionResult> getResults() { return results; }
}
