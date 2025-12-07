package com.example.englishapp.model;

public class CountsResponse {
    private boolean checkedInToday;
    private int totalDaysCheckedIn;
    private int weekCount;
    private int monthCount;

    public boolean isCheckedInToday() { return checkedInToday; }
    public int getTotalDaysCheckedIn() { return totalDaysCheckedIn; }
    public int getWeekCount() { return weekCount; }
    public int getMonthCount() { return monthCount; }
}

