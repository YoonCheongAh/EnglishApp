package com.doanchuyennganh.duong.dto;

public class CountsResponse {
    public long weekCount;
    public long monthCount;

    public long getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(long weekCount) {
        this.weekCount = weekCount;
    }

    public long getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(long monthCount) {
        this.monthCount = monthCount;
    }
}
