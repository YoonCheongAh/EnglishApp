package com.example.englishapp.ui.theme;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.englishapp.R;
import com.example.englishapp.api.AttendanceApi;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.model.CheckinResponse;
import com.example.englishapp.model.DatesResponse;
import com.example.englishapp.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.*;

public class AttendanceBottomSheet extends BottomSheetDialogFragment {

    private CalendarView calendarView;
    private Button btnCheckin;
    private TextView tvMonth;
    private AttendanceApi attendanceApi;
    private SessionManager sessionManager;
    private List<String> checkedInDates = new ArrayList<>();
    private Calendar currentMonth = Calendar.getInstance();
    private Handler handler = new Handler(Looper.getMainLooper());

    public interface OnCheckinListener {
        void onCheckinSuccess();
    }

    private OnCheckinListener listener;

    public void setOnCheckinListener(OnCheckinListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_attendance, container, false);

        calendarView = view.findViewById(R.id.calendar_view);
        btnCheckin = view.findViewById(R.id.btn_checkin);
        tvMonth = view.findViewById(R.id.tv_month);
        View btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        View btnNextMonth = view.findViewById(R.id.btn_next_month);

        sessionManager = new SessionManager(getContext());
        attendanceApi = RetrofitClient.getInstance().create(AttendanceApi.class);

        updateMonthDisplay();
        loadAttendanceDates();

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateMonthDisplay();
            loadAttendanceDates();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateMonthDisplay();
            loadAttendanceDates();
        });

        btnCheckin.setOnClickListener(v -> performCheckin());

        return view;
    }

    private void updateMonthDisplay() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        tvMonth.setText(format.format(currentMonth.getTime()));

        if (calendarView != null) {
            calendarView.setMonth(currentMonth.get(Calendar.YEAR), currentMonth.get(Calendar.MONTH));
        }
    }

    private void loadAttendanceDates() {
        Long userId = sessionManager.getUserId();
        if (userId == null) return;

        int userIdInt = userId.intValue();

        attendanceApi.getDates(userIdInt).enqueue(new Callback<DatesResponse>() {
            @Override
            public void onResponse(Call<DatesResponse> call, Response<DatesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> apiDates = response.body().getDates();
                    if (apiDates == null) {
                        apiDates = new ArrayList<>();
                    }

                    // Convert API dates (yyyy-MM-dd) to format calendar needs
                    List<String> formattedDates = new ArrayList<>();
                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (String dateStr : apiDates) {
                        try {
                            // Parse the date string
                            Date date = apiFormat.parse(dateStr);
                            if (date != null) {
                                // Format back to yyyy-MM-dd for CalendarView
                                formattedDates.add(apiFormat.format(date));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    checkedInDates = formattedDates;

                    if (calendarView != null) {
                        calendarView.setCheckedDates(checkedInDates);
                    }
                }
            }

            @Override
            public void onFailure(Call<DatesResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void performCheckin() {
        Long userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        int userIdInt = userId.intValue();
        Map<String, Integer> body = new HashMap<>();
        body.put("userId", userIdInt);

        btnCheckin.setEnabled(false);
        btnCheckin.setText("Đang xử lý...");

        attendanceApi.checkin(body).enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(Call<CheckinResponse> call, Response<CheckinResponse> response) {
                btnCheckin.setEnabled(true);
                btnCheckin.setText("Điểm danh");

                if (response.isSuccessful() && response.body() != null) {
                    CheckinResponse result = response.body();
                    if (result.isSuccess()) {
                        Toast.makeText(getContext(), "✓ " + result.getMessage(), Toast.LENGTH_SHORT).show();

                        // Reload calendar to show new checked date
                        loadAttendanceDates();

                        // Notify listener
                        if (listener != null) {
                            listener.onCheckinSuccess();
                        }

                        // Dismiss after short delay to show updated calendar
                        // FIX: Use handler instead of view.postDelayed()
                        handler.postDelayed(() -> {
                            if (isAdded()) { // Check if fragment is still attached
                                dismiss();
                            }
                        }, 500);
                    } else {
                        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể điểm danh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckinResponse> call, Throwable t) {
                btnCheckin.setEnabled(true);
                btnCheckin.setText("Điểm danh");
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}