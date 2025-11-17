package com.example.englishapp.screen;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.adapter.DailyWordAdapter;
import com.example.englishapp.api.AttendanceApi;
import com.example.englishapp.api.DailyWordService;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.model.CheckinResponse;
import com.example.englishapp.model.CountsResponse;
import com.example.englishapp.model.DailyWordDTO;
import com.example.englishapp.model.TodayWordsResponseDTO;
import com.example.englishapp.utils.SessionManager;
import com.example.englishapp.ui.theme.AttendanceBottomSheet;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DailyWordAdapter adapter;
    private Button btnLearnMore;
    private TextView tvTotalWords, tvLearnedWords;
    private DailyWordService dailyWordService;
    private AttendanceApi attendanceApi;
    private List<DailyWordDTO> currentWordList;
    private MediaPlayer mediaPlayer;
    private Gson gson = new Gson();

    // Attendance views
    private CardView attendanceCard;
    private TextView tvDate, tvWeekCount, tvMonthCount;

    // Detail card views
    private CardView detailCard;
    private ImageView detailImage, detailBtnAudio, btnCloseDetail;
    private TextView detailWord, detailPhonetic, detailWordType, detailTopic;
    private TextView detailMeaningVn, detailMeaningEn;
    private TextView detailMeaningVnLabel, detailMeaningEnLabel;

    private SessionManager sessionManager;
    private boolean hasCheckedInToday = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        sessionManager = new SessionManager(getContext());
        dailyWordService = RetrofitClient.getInstance().create(DailyWordService.class);
        attendanceApi = RetrofitClient.getInstance().create(AttendanceApi.class);
        currentWordList = new ArrayList<>();

        setupRecyclerView();
        setupAttendance();
        loadTodayWords();
        loadAttendanceStats();

        btnLearnMore.setOnClickListener(v -> loadMoreWords());
        btnCloseDetail.setOnClickListener(v -> hideDetailCard());

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.daily_words_recycler);
        btnLearnMore = view.findViewById(R.id.learn_more_button);
        tvTotalWords = view.findViewById(R.id.tv_total_words);
        tvLearnedWords = view.findViewById(R.id.tv_learned_words);

        // Attendance views
        attendanceCard = view.findViewById(R.id.attendance_card);
        tvDate = view.findViewById(R.id.tv_date);
        tvWeekCount = view.findViewById(R.id.tv_week_count);
        tvMonthCount = view.findViewById(R.id.tv_month_count);

        // Detail card
        detailCard = view.findViewById(R.id.detail_card);
        detailImage = view.findViewById(R.id.detail_image);
        detailBtnAudio = view.findViewById(R.id.detail_btn_audio);
        btnCloseDetail = view.findViewById(R.id.btn_close_detail);
        detailWord = view.findViewById(R.id.detail_word);
        detailPhonetic = view.findViewById(R.id.detail_phonetic);
        detailWordType = view.findViewById(R.id.detail_word_type);
        detailTopic = view.findViewById(R.id.detail_topic);
        detailMeaningVn = view.findViewById(R.id.detail_meaning_vn);
        detailMeaningEn = view.findViewById(R.id.detail_meaning_en);
        detailMeaningVnLabel = view.findViewById(R.id.detail_meaning_vn_label);
        detailMeaningEnLabel = view.findViewById(R.id.detail_meaning_en_label);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DailyWordAdapter(getContext(),
                this::showDetailCard,
                this::markAsLearnedAndRemove
        );
        recyclerView.setAdapter(adapter);
    }

    private void setupAttendance() {
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));

        // Click listener to open calendar bottom sheet
        attendanceCard.setOnClickListener(v -> showAttendanceBottomSheet());
    }

    private void showAttendanceBottomSheet() {
        AttendanceBottomSheet bottomSheet = new AttendanceBottomSheet();
        bottomSheet.setOnCheckinListener(() -> {
            // Reload stats after successful check-in
            loadAttendanceStats();
        });
        bottomSheet.show(getChildFragmentManager(), "attendance_bottom_sheet");
    }

    private void loadAttendanceStats() {
        Long userId = sessionManager.getUserId();
        if (userId == null) return;

        int userIdInt = userId.intValue();

        attendanceApi.getCounts(userIdInt).enqueue(new Callback<CountsResponse>() {
            @Override
            public void onResponse(Call<CountsResponse> call, Response<CountsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CountsResponse counts = response.body();

                    hasCheckedInToday = counts.isCheckedInToday();
                    int totalDays = counts.getTotalDaysCheckedIn();

                    // Calculate week and month stats
                    // For simplicity, showing total days for both
                    // You can enhance this by calling separate endpoints if available
                    tvWeekCount.setText(totalDays + " ngày");
                    tvMonthCount.setText(totalDays + " ngày");

                    // Update card appearance if already checked in
                    if (hasCheckedInToday) {
                        attendanceCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                }
            }

            @Override
            public void onFailure(Call<CountsResponse> call, Throwable t) {
                // Silently fail for stats
            }
        });
    }

    private void loadTodayWords() {
        dailyWordService.getTodayWords().enqueue(new Callback<TodayWordsResponseDTO>() {
            @Override
            public void onResponse(Call<TodayWordsResponseDTO> call, Response<TodayWordsResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TodayWordsResponseDTO data = response.body();

                    currentWordList.clear();
                    if (data.getWords() != null) {
                        for (DailyWordDTO word : data.getWords()) {
                            if (word.getIsLearned() == null || !word.getIsLearned()) {
                                currentWordList.add(word);
                            }
                        }
                    }

                    updateUI();

                    if (currentWordList.isEmpty()) {
                        Toast.makeText(getContext(), "Bạn đã học hết từ hôm nay! 🎉", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải từ vựng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodayWordsResponseDTO> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreWords() {
        dailyWordService.refreshTodayWords().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> data = response.body();

                        if (data.containsKey("words")) {
                            String wordsJson = gson.toJson(data.get("words"));
                            DailyWordDTO[] wordsArray = gson.fromJson(wordsJson, DailyWordDTO[].class);

                            currentWordList.clear();
                            for (DailyWordDTO word : wordsArray) {
                                if (word.getIsLearned() == null || !word.getIsLearned()) {
                                    currentWordList.add(word);
                                }
                            }

                            updateUI();
                            Toast.makeText(getContext(), "Đã làm mới từ vựng!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể làm mới", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsLearnedAndRemove(DailyWordDTO word, int position) {
        if (position < 0 || position >= currentWordList.size()) {
            Toast.makeText(getContext(), "Lỗi: Vị trí không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (detailCard.getVisibility() == View.VISIBLE) {
            hideDetailCard();
        }

        dailyWordService.markAsLearned(word.getDailyWordId(), true)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                int currentPosition = -1;
                                for (int i = 0; i < currentWordList.size(); i++) {
                                    if (currentWordList.get(i).getDailyWordId().equals(word.getDailyWordId())) {
                                        currentPosition = i;
                                        break;
                                    }
                                }

                                if (currentPosition >= 0) {
                                    currentWordList.remove(currentPosition);
                                    adapter.notifyItemRemoved(currentPosition);
                                    updateUI();

                                    Toast.makeText(getContext(),
                                            "✓ Đã học từ \"" + word.getWord() + "\"",
                                            Toast.LENGTH_SHORT).show();

                                    if (currentWordList.isEmpty()) {
                                        Toast.makeText(getContext(),
                                                "🎉 Bạn đã học hết từ hôm nay!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    loadTodayWords();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                                loadTodayWords();
                            }
                        } else {
                            Toast.makeText(getContext(), "Không thể đánh dấu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        adapter.setWords(new ArrayList<>(currentWordList));
        int totalWords = currentWordList.size();
        tvTotalWords.setText(totalWords + " từ chưa học");
        tvLearnedWords.setText("Còn lại: " + totalWords + " từ");
    }

    private void showDetailCard(DailyWordDTO word, int position) {
        detailWord.setText(word.getWord());

        if (word.getPhonetic() != null && !word.getPhonetic().isEmpty()) {
            detailPhonetic.setText("/" + word.getPhonetic() + "/");
            detailPhonetic.setVisibility(View.VISIBLE);
        } else {
            detailPhonetic.setVisibility(View.GONE);
        }

        detailWordType.setText(word.getWordType());

        if (word.getTopic() != null && !word.getTopic().isEmpty()) {
            detailTopic.setText(word.getTopic());
            detailTopic.setVisibility(View.VISIBLE);
        } else {
            detailTopic.setVisibility(View.GONE);
        }

        updateMeaningFields(word);

        if (word.getImageUrl() != null && !word.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(word.getImageUrl())
                    .placeholder(R.drawable.default_avatar)
                    .into(detailImage);
        } else {
            detailImage.setImageResource(R.drawable.default_avatar);
        }

        detailBtnAudio.setOnClickListener(v -> playAudio(word.getAudioUrl()));

        detailCard.setVisibility(View.VISIBLE);
        detailCard.setAlpha(0f);
        detailCard.setScaleY(0.8f);
        detailCard.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();
    }

    private void updateMeaningFields(DailyWordDTO word) {
        if (word.getMeaningVn() != null && !word.getMeaningVn().isEmpty()) {
            detailMeaningVnLabel.setVisibility(View.VISIBLE);
            detailMeaningVn.setVisibility(View.VISIBLE);
            detailMeaningVn.setText(word.getMeaningVn());
        } else {
            detailMeaningVnLabel.setVisibility(View.GONE);
            detailMeaningVn.setVisibility(View.GONE);
        }

        if (word.getMeaningEn() != null && !word.getMeaningEn().isEmpty()) {
            detailMeaningEnLabel.setVisibility(View.VISIBLE);
            detailMeaningEn.setVisibility(View.VISIBLE);
            detailMeaningEn.setText(word.getMeaningEn());
        } else {
            detailMeaningEnLabel.setVisibility(View.GONE);
            detailMeaningEn.setVisibility(View.GONE);
        }
    }

    private void hideDetailCard() {
        detailCard.animate()
                .alpha(0f)
                .scaleY(0.8f)
                .setDuration(200)
                .withEndAction(() -> detailCard.setVisibility(View.GONE))
                .start();
    }

    private void playAudio(String audioUrl) {
        if (audioUrl == null || audioUrl.isEmpty()) {
            Toast.makeText(getContext(), "Không có âm thanh", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl.trim());

            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
                mp.release();
                mediaPlayer = null;
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodayWords();
        loadAttendanceStats();
    }
}