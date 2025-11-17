package com.example.englishapp.ui.theme;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarView extends GridLayout {

    private int currentYear;
    private int currentMonth;
    private Set<Integer> checkedDays = new HashSet<>();
    private Calendar calendar = Calendar.getInstance();

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setColumnCount(7);
        setRowCount(7); // 1 header + max 6 weeks
    }

    public void setMonth(int year, int month) {
        this.currentYear = year;
        this.currentMonth = month;
        buildCalendar();
    }

    public void setCheckedDates(List<String> dates) {
        checkedDays.clear();

        if (dates == null || dates.isEmpty()) {
            buildCalendar();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        for (String dateStr : dates) {
            try {
                Date d = sdf.parse(dateStr);
                if (d != null) {
                    cal.setTime(d);
                    // Only mark days in current displayed month
                    if (cal.get(Calendar.YEAR) == currentYear &&
                            cal.get(Calendar.MONTH) == currentMonth) {
                        checkedDays.add(cal.get(Calendar.DAY_OF_MONTH));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        buildCalendar();
    }

    private void buildCalendar() {
        removeAllViews();

        // Add day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            addHeader(dayName);
        }

        // Calculate calendar
        calendar.set(currentYear, currentMonth, 1);
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0-based
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells before first day
        for (int i = 0; i < firstDay; i++) {
            addEmptyCell();
        }

        // Add day cells
        for (int day = 1; day <= daysInMonth; day++) {
            boolean isToday = isToday(day);
            boolean isChecked = checkedDays.contains(day);
            addDayCell(day, isChecked, isToday);
        }
    }

    private void addHeader(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.parseColor("#999999"));
        tv.setTextSize(12);

        LayoutParams params = new LayoutParams();
        params.width = 0;
        params.height = LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 8, 4, 8);

        tv.setLayoutParams(params);
        addView(tv);
    }

    private void addEmptyCell() {
        View v = new View(getContext());
        LayoutParams params = new LayoutParams();
        params.width = 0;
        params.height = 64;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        v.setLayoutParams(params);
        addView(v);
    }

    private void addDayCell(int day, boolean isChecked, boolean isToday) {
        CardView card = new CardView(getContext());
        card.setRadius(12);
        card.setCardElevation(2);

        // Set background color
        if (isChecked) {
            card.setCardBackgroundColor(Color.parseColor("#4A56E2")); // Blue for checked
        } else if (isToday) {
            card.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // Light orange for today
        } else {
            card.setCardBackgroundColor(Color.WHITE);
        }

        // Create text view for day number
        TextView text = new TextView(getContext());
        text.setText(String.valueOf(day));
        text.setGravity(Gravity.CENTER);
        text.setTextSize(14);
        text.setTextColor(isChecked ? Color.WHITE : Color.parseColor("#333333"));

        card.addView(text);

        // Layout params
        LayoutParams params = new LayoutParams();
        params.width = 0;
        params.height = 64;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        card.setLayoutParams(params);

        addView(card);
    }

    private boolean isToday(int day) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == currentYear &&
                today.get(Calendar.MONTH) == currentMonth &&
                today.get(Calendar.DAY_OF_MONTH) == day;
    }
}