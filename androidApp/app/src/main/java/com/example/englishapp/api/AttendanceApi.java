package com.example.englishapp.api;

import com.example.englishapp.model.CheckinResponse;
import com.example.englishapp.model.CountsResponse;
import com.example.englishapp.model.DatesResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AttendanceApi {
    @POST("api/attendance/checkin")
    Call<CheckinResponse> checkin(@Body Map<String, Integer> body);

    @GET("api/attendance/counts")
    Call<CountsResponse> getCounts(@Query("userId") int userId);

    @GET("api/attendance/dates")
    Call<DatesResponse> getDates(@Query("userId") int userId);
}
