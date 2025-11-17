package com.doanchuyennganh.duong.dto;

import java.util.List;

public class BulkMarkLearnedRequest {
    private List<Integer> dailyWordIds;
    private Boolean learned;

    public BulkMarkLearnedRequest() {}

    public BulkMarkLearnedRequest(List<Integer> dailyWordIds, Boolean learned) {
        this.dailyWordIds = dailyWordIds;
        this.learned = learned;
    }

    public List<Integer> getDailyWordIds() { return dailyWordIds; }
    public void setDailyWordIds(List<Integer> dailyWordIds) { this.dailyWordIds = dailyWordIds; }

    public Boolean getLearned() { return learned; }
    public void setLearned(Boolean learned) { this.learned = learned; }
}
