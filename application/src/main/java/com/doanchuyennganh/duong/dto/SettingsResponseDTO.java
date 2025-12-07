package com.doanchuyennganh.duong.dto;

public class SettingsResponseDTO {
    private Integer settingId;
    private Integer userId;
    private Boolean notificationEnabled;
    private Boolean soundEnabled;
    private Boolean darkModeEnabled;
    private Boolean autoPlayEnabled;
    private Integer fontSize;
    private Integer dailyGoal;
    private String message;

    public SettingsResponseDTO() {}

    public SettingsResponseDTO(Integer settingId, Integer userId, Boolean notificationEnabled,
                               Boolean soundEnabled, Boolean darkModeEnabled, Boolean autoPlayEnabled,
                               Integer fontSize, Integer dailyGoal, String message) {
        this.settingId = settingId;
        this.userId = userId;
        this.notificationEnabled = notificationEnabled;
        this.soundEnabled = soundEnabled;
        this.darkModeEnabled = darkModeEnabled;
        this.autoPlayEnabled = autoPlayEnabled;
        this.fontSize = fontSize;
        this.dailyGoal = dailyGoal;
        this.message = message;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

