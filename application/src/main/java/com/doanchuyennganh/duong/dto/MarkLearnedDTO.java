package com.doanchuyennganh.duong.dto;

public class MarkLearnedDTO {
    private Integer dailyWordId;
    private Boolean isLearned;

    public MarkLearnedDTO() {}

    public MarkLearnedDTO(Integer dailyWordId, Boolean isLearned) {
        this.dailyWordId = dailyWordId;
        this.isLearned = isLearned;
    }

    public Integer getDailyWordId() { return dailyWordId; }
    public void setDailyWordId(Integer dailyWordId) { this.dailyWordId = dailyWordId; }

    public Boolean getIsLearned() { return isLearned; }
    public void setIsLearned(Boolean isLearned) { this.isLearned = isLearned; }
}
