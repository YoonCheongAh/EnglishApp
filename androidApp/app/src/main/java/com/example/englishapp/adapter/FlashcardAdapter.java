package com.example.englishapp.adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.model.FlashcardResponse;

import java.util.ArrayList;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {

    private List<FlashcardResponse> flashcards = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FlashcardResponse flashcard);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setFlashcards(List<FlashcardResponse> list) {
        this.flashcards = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlashcardResponse flashcard = flashcards.get(position);

        holder.tvWord.setText(flashcard.getWord());
        holder.tvWordType.setText(flashcard.getWordType());
        holder.tvTopic.setText(flashcard.getTopic());
        holder.tvPhonetic.setText("/" + flashcard.getPhonetic() + "/");

        String meaning = flashcard.getMeaningVN() != null && !flashcard.getMeaningVN().isEmpty()
                ? flashcard.getMeaningVN()
                : flashcard.getMeaningEn();
        holder.tvMeaning.setText(meaning);

        if (flashcard.getImageUrl() != null && !flashcard.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(flashcard.getImageUrl())
                    .placeholder(R.drawable.searching)
                    .error(R.drawable.empty)
                    .into(holder.ivFlashcardImage);
        }

        // Click to show detail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(flashcard);
            }
        });

        holder.btnPlayAudio.setOnClickListener(v -> {
            playAudio(flashcard.getAudioUrl());
            // Prevent card click when clicking audio button
            v.setOnClickListener(view -> playAudio(flashcard.getAudioUrl()));
        });
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlashcardImage, btnPlayAudio;
        TextView tvWord, tvWordType, tvTopic, tvPhonetic, tvMeaning;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlashcardImage = itemView.findViewById(R.id.ivFlashcardImage);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvWordType = itemView.findViewById(R.id.tvWordType);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
        }
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

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}