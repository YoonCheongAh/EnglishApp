package com.example.englishapp.api;

import com.example.englishapp.model.SettingsDTO;
import com.example.englishapp.model.UserSettings;
import retrofit2.Call;
import retrofit2.http.*;

public interface SettingsService {

    @GET("api/settings")
    Call<UserSettings> getSettings();

    @PUT("api/settings")
    Call<UserSettings> updateSettings(@Body SettingsDTO settingsDTO);

    @PATCH("api/settings/notification")
    Call<UserSettings> updateNotification(@Query("enabled") Boolean enabled);

    @PATCH("api/settings/daily-goal")
    Call<UserSettings> updateDailyGoal(@Query("goal") Integer goal);

    @PATCH("api/settings/font-size")
    Call<UserSettings> updateFontSize(@Query("fontSize") Integer fontSize);

    @POST("api/settings/reset")
    Call<UserSettings> resetSettings();

    @GET("api/settings/daily-goal")
    Call<Integer> getDailyGoal();
}
