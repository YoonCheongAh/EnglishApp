package com.example.englishapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.model.DailyWordDTO;
import java.util.ArrayList;
import java.util.List;

public class LearnedWordsAdapter extends RecyclerView.Adapter<LearnedWordsAdapter.ViewHolder> {

    private Context context;
    private List<DailyWordDTO> words;
    private OnItemClickListener clickListener;
    private OnUnlearnClickListener unlearnListener;

    public interface OnItemClickListener {
        void onItemClick(DailyWordDTO word, int position);
    }

    public interface OnUnlearnClickListener {
        void onUnlearnClick(DailyWordDTO word, int position);
    }

    public LearnedWordsAdapter(Context context, OnItemClickListener clickListener, OnUnlearnClickListener unlearnListener) {
        this.context = context;
        this.words = new ArrayList<>();
        this.clickListener = clickListener;
        this.unlearnListener = unlearnListener;
    }

    public void setWords(List<DailyWordDTO> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_learned_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyWordDTO word = words.get(position);

        holder.tvWord.setText(word.getWord());

        if (word.getPhonetic() != null && !word.getPhonetic().isEmpty()) {
            holder.tvPhonetic.setText("/" + word.getPhonetic() + "/");
            holder.tvPhonetic.setVisibility(View.VISIBLE);
        } else {
            holder.tvPhonetic.setVisibility(View.GONE);
        }

        holder.tvWordType.setText(word.getWordType());

        if (word.getTopic() != null && !word.getTopic().isEmpty()) {
            holder.tvTopic.setText(word.getTopic());
            holder.tvTopic.setVisibility(View.VISIBLE);
        } else {
            holder.tvTopic.setVisibility(View.GONE);
        }

        // Show Vietnamese meaning first, then English
        String meaning = word.getMeaningVn();
        if (meaning == null || meaning.isEmpty()) {
            meaning = word.getMeaningEn();
        }
        holder.tvMeaning.setText(meaning);

        // Show date learned
        if (word.getWordDate() != null) {
            holder.tvDateLearned.setText("Học ngày: " + word.getWordDate());
            holder.tvDateLearned.setVisibility(View.VISIBLE);
        } else {
            holder.tvDateLearned.setVisibility(View.GONE);
        }

        // Load image
        if (word.getImageUrl() != null && !word.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(word.getImageUrl())
                    .placeholder(R.drawable.default_avatar)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.default_avatar);
        }

        // Click on card - show detail
        holder.cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(word, position);
            }
        });

        // Click on unlearn button
        holder.btnUnlearn.setOnClickListener(v -> {
            if (unlearnListener != null) {
                unlearnListener.onUnlearnClick(word, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivImage, btnUnlearn;
        TextView tvWord, tvPhonetic, tvWordType, tvTopic, tvMeaning, tvDateLearned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivImage = itemView.findViewById(R.id.ivFlashcardImage);
            btnUnlearn = itemView.findViewById(R.id.btnUnlearn);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvWordType = itemView.findViewById(R.id.tvWordType);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvDateLearned = itemView.findViewById(R.id.tvDateLearned);
        }
    }
}