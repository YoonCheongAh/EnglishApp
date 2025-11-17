package com.example.englishapp.screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.englishapp.R;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.api.SettingsService;
import com.example.englishapp.model.SettingsDTO;
import com.example.englishapp.model.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchNotification, switchSound, switchDarkMode, switchAutoPlay;
    private SeekBar seekBarFontSize, seekBarDailyGoal;
    private TextView tvFontSizeValue, tvDailyGoalValue;
    private SettingsService settingsService;
    private UserSettings currentSettings;
    private boolean isLoadingSettings = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(view);
        settingsService = RetrofitClient.getInstance().create(SettingsService.class);

        loadSettings();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        switchNotification = view.findViewById(R.id.switch_notification);
        switchSound = view.findViewById(R.id.switch_sound);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchAutoPlay = view.findViewById(R.id.switch_auto_play);

        seekBarFontSize = view.findViewById(R.id.seekbar_font_size);
        tvFontSizeValue = view.findViewById(R.id.tv_font_size_value);

        seekBarDailyGoal = view.findViewById(R.id.seekbar_daily_goal);
        tvDailyGoalValue = view.findViewById(R.id.tv_daily_goal_value);
    }

    private void loadSettings() {
        isLoadingSettings = true;
        settingsService.getSettings().enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                isLoadingSettings = false;
                if (response.isSuccessful() && response.body() != null) {
                    currentSettings = response.body();
                    updateUI(currentSettings);
                } else {
                    Toast.makeText(getContext(), "Không thể tải cài đặt", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                isLoadingSettings = false;
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(UserSettings settings) {
        switchNotification.setChecked(settings.getNotificationEnabled());
        switchSound.setChecked(settings.getSoundEnabled());
        switchDarkMode.setChecked(settings.getDarkModeEnabled());
        switchAutoPlay.setChecked(settings.getAutoPlayEnabled());

        // Font size
        int fontSize = settings.getFontSize();
        if (fontSize < 12) fontSize = 12;
        seekBarFontSize.setProgress(fontSize - 12);
        tvFontSizeValue.setText(fontSize + "sp");

        // Daily goal
        int dailyGoal = settings.getDailyGoal();
        if (dailyGoal < 1) dailyGoal = 1;
        seekBarDailyGoal.setProgress(dailyGoal);
        tvDailyGoalValue.setText(dailyGoal + " từ");
    }

    private void setupListeners() {
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoadingSettings && currentSettings != null) updateNotificationSetting(isChecked);
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoadingSettings && currentSettings != null) updateSoundSetting(isChecked);
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoadingSettings && currentSettings != null) updateDarkModeSetting(isChecked);
        });

        switchAutoPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoadingSettings && currentSettings != null) updateAutoPlaySetting(isChecked);
        });

        // Daily goal listener
        seekBarDailyGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                tvDailyGoalValue.setText(progress + " từ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!isLoadingSettings && currentSettings != null) {
                    int goal = seekBar.getProgress();
                    if (goal < 1) goal = 1;
                    updateDailyGoal(goal);
                }
            }
        });

        // Font size listener
        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int fontSize = progress + 12;
                tvFontSizeValue.setText(fontSize + "sp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!isLoadingSettings && currentSettings != null) {
                    int fontSize = seekBar.getProgress() + 12;
                    updateFontSize(fontSize);
                }
            }
        });
    }

    private void updateNotificationSetting(Boolean enabled) {
        settingsService.updateNotification(enabled).enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.isSuccessful() && response.body() != null) currentSettings = response.body();
                else {
                    switchNotification.setChecked(!enabled);
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                switchNotification.setChecked(!enabled);
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSoundSetting(Boolean enabled) {
        SettingsDTO dto = new SettingsDTO();
        dto.setSoundEnabled(enabled);
        settingsService.updateSettings(dto).enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.isSuccessful() && response.body() != null) currentSettings = response.body();
                else {
                    switchSound.setChecked(!enabled);
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                switchSound.setChecked(!enabled);
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDarkModeSetting(Boolean enabled) {
        SettingsDTO dto = new SettingsDTO();
        dto.setDarkModeEnabled(enabled);
        settingsService.updateSettings(dto).enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.isSuccessful() && response.body() != null) currentSettings = response.body();
                else {
                    switchDarkMode.setChecked(!enabled);
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                switchDarkMode.setChecked(!enabled);
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAutoPlaySetting(Boolean enabled) {
        SettingsDTO dto = new SettingsDTO();
        dto.setAutoPlayEnabled(enabled);
        settingsService.updateSettings(dto).enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.isSuccessful() && response.body() != null) currentSettings = response.body();
                else {
                    switchAutoPlay.setChecked(!enabled);
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                switchAutoPlay.setChecked(!enabled);
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDailyGoal(int goal) {
        settingsService.updateDailyGoal(goal).enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.isSuccessful() && response.body() != null) currentSettings = response.body();
                else Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFontSize(int fontSize) {
        settingsService.updateFontSize(fontSize).enqueue(new Callback<UserSettings>() {
            @Override
            public void onResponse(Call<UserSettings> call, Response<UserSettings> response) {
                if (response.isSuccessful() && response.body() != null) currentSettings = response.body();
                else {
                    Toast.makeText(getContext(), "Cập nhật font size thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSettings> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng khi cập nhật font size", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
