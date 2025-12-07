package com.example.englishapp.screen;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.adapter.FlashcardAdapter;
import com.example.englishapp.api.EnglishService;
import com.example.englishapp.api.ApiClient;
import com.example.englishapp.model.FlashcardResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private ImageView emptyGif;
    private TextView emptyText;
    private EditText searchInput;
    private RecyclerView recyclerView;
    private FlashcardAdapter flashcardAdapter;

    // Detail card views
    private CardView detailCard;
    private ImageView detailImage, detailBtnAudio, btnCloseDetail;
    private TextView detailWord, detailPhonetic, detailWordType, detailTopic;
    private TextView detailMeaningVnLabel, detailMeaningVn, detailMeaningEnLabel, detailMeaningEn;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchInput = view.findViewById(R.id.search_input);
        emptyGif = view.findViewById(R.id.empty_gif);
        emptyText = view.findViewById(R.id.empty_text);
        recyclerView = view.findViewById(R.id.search_results);

        // Detail card views
        detailCard = view.findViewById(R.id.detail_card);
        detailImage = view.findViewById(R.id.detail_image);
        detailWord = view.findViewById(R.id.detail_word);
        detailPhonetic = view.findViewById(R.id.detail_phonetic);
        detailWordType = view.findViewById(R.id.detail_word_type);
        detailTopic = view.findViewById(R.id.detail_topic);
        detailMeaningVnLabel = view.findViewById(R.id.detail_meaning_vn_label);
        detailMeaningVn = view.findViewById(R.id.detail_meaning_vn);
        detailMeaningEnLabel = view.findViewById(R.id.detail_meaning_en_label);
        detailMeaningEn = view.findViewById(R.id.detail_meaning_en);
        detailBtnAudio = view.findViewById(R.id.detail_btn_audio);
        btnCloseDetail = view.findViewById(R.id.btn_close_detail);

        flashcardAdapter = new FlashcardAdapter();
        recyclerView.setAdapter(flashcardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set item click listener
        flashcardAdapter.setOnItemClickListener(this::showDetailCard);

        // Close detail card
        btnCloseDetail.setOnClickListener(v -> hideDetailCard());

        showSearchingState();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    hideDetailCard();
                    showSearchingState();
                } else {
                    searchFlashcards(query);
                }
            }
        });
    }

    private void showDetailCard(FlashcardResponse flashcard) {
        detailCard.setVisibility(View.VISIBLE);
        detailCard.setAlpha(0f);
        detailCard.animate().alpha(1f).setDuration(300).start();

        detailWord.setText(flashcard.getWord());
        detailPhonetic.setText("/" + flashcard.getPhonetic() + "/");
        detailWordType.setText(flashcard.getWordType());
        detailTopic.setText(flashcard.getTopic());

        // Hiển thị nghĩa tiếng Việt nếu có
        if (flashcard.getMeaningVN() != null && !flashcard.getMeaningVN().isEmpty()) {
            detailMeaningVnLabel.setVisibility(View.VISIBLE);
            detailMeaningVn.setVisibility(View.VISIBLE);
            detailMeaningVn.setText(flashcard.getMeaningVN());
        } else {
            detailMeaningVnLabel.setVisibility(View.GONE);
            detailMeaningVn.setVisibility(View.GONE);
        }

        // Hiển thị nghĩa tiếng Anh nếu có
        if (flashcard.getMeaningEn() != null && !flashcard.getMeaningEn().isEmpty()) {
            detailMeaningEnLabel.setVisibility(View.VISIBLE);
            detailMeaningEn.setVisibility(View.VISIBLE);
            detailMeaningEn.setText(flashcard.getMeaningEn());
        } else {
            detailMeaningEnLabel.setVisibility(View.GONE);
            detailMeaningEn.setVisibility(View.GONE);
        }

        if (flashcard.getImageUrl() != null && !flashcard.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(flashcard.getImageUrl())
                    .placeholder(R.drawable.searching)
                    .error(R.drawable.empty)
                    .into(detailImage);
        }

        detailBtnAudio.setOnClickListener(v -> playAudio(flashcard.getAudioUrl()));
    }

    private void hideDetailCard() {
        detailCard.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> detailCard.setVisibility(View.GONE))
                .start();
    }

    private void playAudio(String audioUrl) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setVolume(10.0f, 10.0f);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> mp.release());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fadeIn(View v) {
        v.setAlpha(0f);
        v.setVisibility(View.VISIBLE);
        v.animate().alpha(1f).setDuration(300).start();
    }

    private void fadeOut(View v) {
        v.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> v.setVisibility(View.GONE))
                .start();
    }

    private void searchFlashcards(String keyword) {
        EnglishService.FlashcardApi api = ApiClient.getFlashcardApi();
        api.searchFlashcards(keyword).enqueue(new Callback<List<FlashcardResponse>>() {
            @Override
            public void onResponse(Call<List<FlashcardResponse>> call, Response<List<FlashcardResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FlashcardResponse> list = response.body();
                    if (list.isEmpty()) {
                        fadeOut(recyclerView);
                        fadeIn(emptyGif);
                        fadeIn(emptyText);
                        showEmptyState();
                    } else {
                        fadeOut(emptyGif);
                        fadeOut(emptyText);
                        fadeIn(recyclerView);
                        flashcardAdapter.setFlashcards(list);
                    }
                } else {
                    fadeOut(recyclerView);
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<FlashcardResponse>> call, Throwable t) {
                fadeOut(recyclerView);
                showEmptyState();
            }
        });
    }

    private void showSearchingState() {
        fadeOut(recyclerView);
        fadeIn(emptyGif);
        fadeIn(emptyText);

        Glide.with(this)
                .asGif()
                .load(R.drawable.searching)
                .into(emptyGif);
        emptyText.setText("Tìm kiếm gì đó bên dưới");
    }

    private void showEmptyState() {
        fadeOut(recyclerView);
        fadeIn(emptyGif);
        fadeIn(emptyText);

        Glide.with(this)
                .asGif()
                .load(R.drawable.empty)
                .into(emptyGif);
        emptyText.setText("Không có kết quả tìm kiếm");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flashcardAdapter.releaseMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}