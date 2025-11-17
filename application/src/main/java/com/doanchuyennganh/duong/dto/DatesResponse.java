package com.doanchuyennganh.duong.dto;

import java.time.LocalDate;
import java.util.List;

public class DatesResponse {
    public List<LocalDate> dates;

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }
}
