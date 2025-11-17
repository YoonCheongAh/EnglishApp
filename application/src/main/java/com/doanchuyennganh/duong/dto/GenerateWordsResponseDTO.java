package com.doanchuyennganh.duong.dto;

import java.util.List;

public class GenerateWordsResponseDTO {
    private String message;
    private Integer totalWords;
    private List<DailyWordDTO> words;

    public GenerateWordsResponseDTO() {}

    public GenerateWordsResponseDTO(String message, Integer totalWords, List<DailyWordDTO> words) {
        this.message = message;
        this.totalWords = totalWords;
        this.words = words;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getTotalWords() { return totalWords; }
    public void setTotalWords(Integer totalWords) { this.totalWords = totalWords; }

    public List<DailyWordDTO> getWords() { return words; }
    public void setWords(List<DailyWordDTO> words) { this.words = words; }
}
