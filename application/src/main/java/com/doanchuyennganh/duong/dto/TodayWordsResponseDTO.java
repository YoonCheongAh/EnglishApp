package com.doanchuyennganh.duong.dto;

import java.time.LocalDate;
import java.util.List;

public class TodayWordsResponseDTO {
    private String date;
    private Integer totalWords;
    private Integer learnedWords;
    private Integer progress;
    private List<DailyWordDTO> words;

    public TodayWordsResponseDTO() {}

    public TodayWordsResponseDTO(String date, Integer totalWords, Integer learnedWords, Integer progress, List<DailyWordDTO> words) {
        this.date = date;
        this.totalWords = totalWords;
        this.learnedWords = learnedWords;
        this.progress = progress;
        this.words = words;
    }

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