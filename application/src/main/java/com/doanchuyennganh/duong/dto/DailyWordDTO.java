package com.doanchuyennganh.duong.dto;

import java.time.LocalDate;

public class DailyWordDTO {
    private Integer dailyWordId;
    private Integer flashcardId;
    private String word;
    private String wordType;
    private String topic;
    private String phonetic;
    private String meaningEn;
    private String meaningVn;
    private String imageUrl;
    private String audioUrl;
    private String wordDate;
    private Boolean isLearned;

    public DailyWordDTO() {}

    public DailyWordDTO(Integer dailyWordId, Integer flashcardId, String word, String wordType,
                        String topic, String phonetic, String meaningEn, String meaningVn,
                        String imageUrl, String audioUrl, String wordDate, Boolean isLearned) {
        this.dailyWordId = dailyWordId;
        this.flashcardId = flashcardId;
        this.word = word;
        this.wordType = wordType;
        this.topic = topic;
        this.phonetic = phonetic;
        this.meaningEn = meaningEn;
        this.meaningVn = meaningVn;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.wordDate = wordDate;
        this.isLearned = isLearned;
    }

    // Getter và Setter
    public Integer getDailyWordId() { return dailyWordId; }
    public void setDailyWordId(Integer dailyWordId) { this.dailyWordId = dailyWordId; }

    public Integer getFlashcardId() { return flashcardId; }
    public void setFlashcardId(Integer flashcardId) { this.flashcardId = flashcardId; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getWordType() { return wordType; }
    public void setWordType(String wordType) { this.wordType = wordType; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public String getMeaningEn() { return meaningEn; }
    public void setMeaningEn(String meaningEn) { this.meaningEn = meaningEn; }

    public String getMeaningVn() { return meaningVn; }
    public void setMeaningVn(String meaningVn) { this.meaningVn = meaningVn; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getWordDate() { return wordDate; }
    public void setWordDate(String wordDate) { this.wordDate = wordDate; }

    public Boolean getIsLearned() { return isLearned; }
    public void setIsLearned(Boolean isLearned) { this.isLearned = isLearned; }
}
