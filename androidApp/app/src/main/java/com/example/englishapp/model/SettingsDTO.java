package com.example.englishapp.model;

import com.google.gson.annotations.SerializedName;

public class SettingsDTO {

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

    // Constructors
    public SettingsDTO() {}

    public SettingsDTO(Boolean notificationEnabled, Boolean soundEnabled, Boolean darkModeEnabled,
                       Boolean autoPlayEnabled, Integer fontSize, Integer dailyGoal) {
        this.notificationEnabled = notificationEnabled;
        this.soundEnabled = soundEnabled;
        this.darkModeEnabled = darkModeEnabled;
        this.autoPlayEnabled = autoPlayEnabled;
        this.fontSize = fontSize;
        this.dailyGoal = dailyGoal;
    }

    // Getters and Setters
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
}