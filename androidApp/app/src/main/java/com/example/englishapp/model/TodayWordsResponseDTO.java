package com.example.englishapp.model;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.util.List;

public class TodayWordsResponseDTO {
    @SerializedName("date")
    private String date;

    @SerializedName("totalWords")
    private Integer totalWords;

    @SerializedName("learnedWords")
    private Integer learnedWords;

    @SerializedName("progress")
    private Integer progress;

    @SerializedName("words")
    private List<DailyWordDTO> words;

    public TodayWordsResponseDTO() {}

    // Getter & Setter
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getTotalWords() { return totalWords; }
    public void setTotalWords(Integer totalWords) { this.totalWords = totalWords; }

    public Integer getLearnedWords() { return learnedWords; }
    public void setLearnedWords(Integer learnedWords) { this.learnedWords = learnedWords; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public List<DailyWordDTO> getWords() { return words; }
    public void setWords(List<DailyWordDTO> words) { this.words = words; }
}