package com.example.englishapp.model;

import com.google.gson.annotations.SerializedName;

public class UserSettings {

    @SerializedName("settingId")
    private Integer settingId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("notificationEnabled")
    private Boolean notificationEnabled;

    @SerializedName("soundEnabled")
    private Boolean soundEnabled;

    @SerializedName("darkModeEnabled")
    private Boolean darkModeEnabled;

    @SerializedName("autoPlayEnabled")
    private Boolean autoPlayEnabled;

    @SerializedName("fontSize")
    private Integer fontSize;

    @SerializedName("dailyGoal")
    private Integer dailyGoal;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public UserSettings() {}

    // Getters and Setters
    public Integer getSettingId() {
        return settingId;
    }

    public void setSettingId(Integer settingId) {
        this.settingId = settingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(Boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public Boolean getSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(Boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public Boolean getDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(Boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
    }

    public Boolean getAutoPlayEnabled() {
        return autoPlayEnabled;
    }

    public void setAutoPlayEnabled(Boolean autoPlayEnabled) {
        this.autoPlayEnabled = autoPlayEnabled;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(Integer dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}