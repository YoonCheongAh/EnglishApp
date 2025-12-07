package com.doanchuyennganh.duong.dto;

import java.time.LocalDate;

public class CheckinResponse {
    public boolean success;
    public String message;
    public LocalDate date;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
