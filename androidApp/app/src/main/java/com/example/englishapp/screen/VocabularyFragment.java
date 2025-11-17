package com.example.englishapp.screen;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.example.englishapp.adapter.LearnedWordsAdapter;
import com.example.englishapp.api.DailyWordService;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.model.DailyWordDTO;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VocabularyFragment extends Fragment {

    private RecyclerView recyclerView;
    private LearnedWordsAdapter adapter;
    private List<DailyWordDTO> allLearnedWords;
    private List<DailyWordDTO> filteredWords;
    private SearchView searchView;
    private TextView tvVocabularyCount, tvEmptyMessage;
    private View progressBar;
    private DailyWordService dailyWordService;
    private MediaPlayer mediaPlayer;
    private Gson gson = new Gson();

    // Detail card views
    private CardView detailCard;
    private ImageView detailImage, detailBtnAudio, btnCloseDetail;
    private TextView detailWord, detailPhonetic, detailWordType, detailTopic;
    private TextView detailMeaningVn, detailMeaningEn;
    private TextView detailMeaningVnLabel, detailMeaningEnLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);

        initializeViews(view);
        dailyWordService = RetrofitClient.getInstance().create(DailyWordService.class);

        setupRecyclerView();
        loadLearnedWords();
        setupSearch();

        btnCloseDetail.setOnClickListener(v -> hideDetailCard());

        return view;
    }

    private void initializeViews(View view) {
        searchView = view.findViewById(R.id.search_vocabulary);
        recyclerView = view.findViewById(R.id.recycler_vocabulary);
        tvVocabularyCount = view.findViewById(R.id.vocabulary_count);
        tvEmptyMessage = view.findViewById(R.id.empty_message);
        progressBar = view.findViewById(R.id.progress_bar);

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
        allLearnedWords = new ArrayList<>();
        filteredWords = new ArrayList<>();

        adapter = new LearnedWordsAdapter(getContext(),
                // On item click - show detail
                (word, position) -> showDetailCard(word),
                // On unlearn click - remove from learned
                this::unlearnWord
        );
        recyclerView.setAdapter(adapter);
    }

    private void loadLearnedWords() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyMessage.setVisibility(View.GONE);

        dailyWordService.getAllLearnedWords().enqueue(new Callback<List<DailyWordDTO>>() {
            @Override
            public void onResponse(Call<List<DailyWordDTO>> call, Response<List<DailyWordDTO>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    allLearnedWords.clear();
                    allLearnedWords.addAll(response.body());

                    filteredWords.clear();
                    filteredWords.addAll(allLearnedWords);

                    adapter.setWords(filteredWords);
                    updateVocabularyCount();

                    if (allLearnedWords.isEmpty()) {
                        showEmptyState();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DailyWordDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterWords(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterWords(newText);
                return false;
            }
        });
    }

    private void filterWords(String query) {
        filteredWords.clear();

        if (query.isEmpty()) {
            filteredWords.addAll(allLearnedWords);
        } else {
            String lowerQuery = query.toLowerCase();
            filteredWords.addAll(
                    allLearnedWords.stream()
                            .filter(word ->
                                    word.getWord().toLowerCase().contains(lowerQuery) ||
                                            (word.getMeaningVn() != null && word.getMeaningVn().toLowerCase().contains(lowerQuery)) ||
                                            (word.getTopic() != null && word.getTopic().toLowerCase().contains(lowerQuery))
                            )
                            .collect(Collectors.toList())
            );
        }

        adapter.notifyDataSetChanged();

        if (filteredWords.isEmpty() && !query.isEmpty()) {
            tvEmptyMessage.setText("Không tìm thấy từ \"" + query + "\"");
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
        }
    }

    private void unlearnWord(DailyWordDTO word, int position) {
        // Validate position trước khi thực hiện
        if (position < 0 || position >= filteredWords.size()) {
            Toast.makeText(getContext(), "Lỗi: Vị trí không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy từ tại position để đảm bảo đúng
        DailyWordDTO wordToUnlearn = filteredWords.get(position);

        dailyWordService.markAsLearned(wordToUnlearn.getDailyWordId(), false)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                // Xóa khỏi cả 2 list
                                allLearnedWords.removeIf(w -> w.getDailyWordId().equals(wordToUnlearn.getDailyWordId()));

                                // Tìm vị trí hiện tại trong filteredWords (có thể đã thay đổi)
                                int currentPosition = -1;
                                for (int i = 0; i < filteredWords.size(); i++) {
                                    if (filteredWords.get(i).getDailyWordId().equals(wordToUnlearn.getDailyWordId())) {
                                        currentPosition = i;
                                        break;
                                    }
                                }

                                if (currentPosition >= 0) {
                                    filteredWords.remove(currentPosition);
                                    adapter.notifyItemRemoved(currentPosition);
                                } else {
                                    // Fallback: reload toàn bộ adapter
                                    adapter.setWords(filteredWords);
                                }

                                updateVocabularyCount();
                                Toast.makeText(getContext(), "Đã bỏ đánh dấu từ \"" + wordToUnlearn.getWord() + "\"", Toast.LENGTH_SHORT).show();

                                if (allLearnedWords.isEmpty()) {
                                    showEmptyState();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                                // Reload lại để đảm bảo đồng bộ
                                loadLearnedWords();
                            }
                        } else {
                            Toast.makeText(getContext(), "Không thể bỏ đánh dấu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDetailCard(DailyWordDTO word) {
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

        // Meanings
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

        // Load image
        if (word.getImageUrl() != null && !word.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(word.getImageUrl())
                    .placeholder(R.drawable.default_avatar)
                    .into(detailImage);
        } else {
            detailImage.setImageResource(R.drawable.default_avatar);
        }

        // Audio button
        detailBtnAudio.setOnClickListener(v -> playAudio(word.getAudioUrl()));

        // Show with animation
        detailCard.setVisibility(View.VISIBLE);
        detailCard.setAlpha(0f);
        detailCard.setScaleY(0.8f);
        detailCard.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();
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

            mediaPlayer.setOnPreparedListener(mp -> mp.start());
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

    private void updateVocabularyCount() {
        tvVocabularyCount.setText("Tổng: " + allLearnedWords.size() + " từ đã học");
    }

    private void showEmptyState() {
        tvEmptyMessage.setText("Bạn chưa học từ nào.\nHãy bắt đầu học từ ở trang chủ!");
        tvEmptyMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}