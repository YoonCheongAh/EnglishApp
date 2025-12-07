package com.example.englishapp.model;
import java.util.List;

public class QuizQuestion {
    private Integer dailyWordId;
    private String word;
    private String correctAnswer;
    private List<String> options;
    private String imageUrl;
    private String audioUrl;
    private String wordType;

    // User's selected answer (không có trong API response)
    private String selectedAnswer;

    // Getters and Setters
    public Integer getDailyWordId() { return dailyWordId; }
    public void setDailyWordId(Integer id) { this.dailyWordId = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String answer) { this.correctAnswer = answer; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String url) { this.imageUrl = url; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String url) { this.audioUrl = url; }

    public String getWordType() { return wordType; }
    public void setWordType(String type) { this.wordType = type; }

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String answer) { this.selectedAnswer = answer; }
}

