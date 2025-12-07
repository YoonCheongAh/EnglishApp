package com.example.englishapp.model;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class DailyWordDTO {
    @SerializedName("dailyWordId")
    private Integer dailyWordId;

    @SerializedName("flashcardId")
    private Integer flashcardId;

    @SerializedName("word")
    private String word;

    @SerializedName("wordType")
    private String wordType;

    @SerializedName("topic")
    private String topic;

    @SerializedName("phonetic")
    private String phonetic;

    @SerializedName("meaningEn")
    private String meaningEn;

    @SerializedName("meaningVn")
    private String meaningVn;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("audioUrl")
    private String audioUrl;

    @SerializedName("wordDate")
    private String wordDate;

    @SerializedName("isLearned")
    private Boolean isLearned;

    public DailyWordDTO() {}

    // Getter & Setter
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
