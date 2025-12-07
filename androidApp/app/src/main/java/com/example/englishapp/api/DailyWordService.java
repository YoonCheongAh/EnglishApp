package com.example.englishapp.api;

import com.example.englishapp.model.DailyWordDTO;
import com.example.englishapp.model.TodayWordsResponseDTO;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface DailyWordService {

    /**
     * Lấy danh sách từ của ngày hôm nay
     */
    @GET("api/daily-words/today")
    Call<TodayWordsResponseDTO> getTodayWords();

    /**
     * Tạo từ mới cho ngày hôm nay
     */
    @POST("api/daily-words/generate")
    Call<TodayWordsResponseDTO> generateTodayWords();

    /**
     * Làm mới từ hôm nay (xóa và tạo lại)
     */
    @POST("api/daily-words/refresh")
    Call<Map<String, Object>> refreshTodayWords();

    /**
     * Đánh dấu một từ đã học
     * Backend trả về Map với key "word" chứa DailyWordDTO
     */
    @PATCH("api/daily-words/{dailyWordId}/learned")
    Call<Map<String, Object>> markAsLearned(
            @Path("dailyWordId") Integer dailyWordId,
            @Query("learned") Boolean learned);

    /**
     * Đánh dấu nhiều từ cùng lúc
     */
    @POST("api/daily-words/bulk-learned")
    Call<Map<String, Object>> markMultipleAsLearned(
            @Body BulkMarkLearnedRequest request);

    /**
     * Lấy thống kê học tập
     */
    @GET("api/daily-words/statistics")
    Call<Map<String, Object>> getStatistics(@Query("date") String date);

    /**
     * Lấy tất cả từ đã học
     */
    @GET("api/daily-words/learned")
    Call<List<DailyWordDTO>> getAllLearnedWords();
}

// Request body for bulk mark learned
class BulkMarkLearnedRequest {
    private List<Integer> dailyWordIds;
    private Boolean learned;

    public BulkMarkLearnedRequest(List<Integer> dailyWordIds, Boolean learned) {
        this.dailyWordIds = dailyWordIds;
        this.learned = learned;
    }

    public List<Integer> getDailyWordIds() {
        return dailyWordIds;
    }

    public void setDailyWordIds(List<Integer> dailyWordIds) {
        this.dailyWordIds = dailyWordIds;
    }

    public Boolean getLearned() {
        return learned;
    }

    public void setLearned(Boolean learned) {
        this.learned = learned;
    }
}