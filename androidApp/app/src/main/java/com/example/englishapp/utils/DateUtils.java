package com.example.englishapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(date);
    }

    public static String formatDateForApi(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    public static String getTodayFormatted() {
        return formatDate(new Date());
    }

    public static String getTodayForApi() {
        return formatDateForApi(new Date());
    }

    public static int getCurrentWeekNumber() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String getDayOfWeek() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        return format.format(new Date());
    }
}