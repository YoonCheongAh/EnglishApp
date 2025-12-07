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

public class DailyWordAdapter extends RecyclerView.Adapter<DailyWordAdapter.ViewHolder> {

    private Context context;
    private List<DailyWordDTO> words;
    private OnItemClickListener clickListener;
    private OnMarkLearnedListener markLearnedListener;

    public interface OnItemClickListener {
        void onItemClick(DailyWordDTO word, int position);
    }

    public interface OnMarkLearnedListener {
        void onMarkLearned(DailyWordDTO word, int position);
    }

    public DailyWordAdapter(Context context, OnItemClickListener clickListener, OnMarkLearnedListener markLearnedListener) {
        this.context = context;
        this.words = new ArrayList<>();
        this.clickListener = clickListener;
        this.markLearnedListener = markLearnedListener;
    }

    public void setWords(List<DailyWordDTO> words) {
        this.words.clear();
        if (words != null) {
            this.words.addAll(words);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_word, parent, false);
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

        String meaning = word.getMeaningVn();
        if (meaning == null || meaning.isEmpty()) {
            meaning = word.getMeaningEn();
        }
        holder.tvMeaning.setText(meaning);

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
                clickListener.onItemClick(word, holder.getAdapterPosition());
            }
        });

        // Click on check button - mark as learned with animation
        holder.btnMarkLearned.setOnClickListener(v -> {
            if (markLearnedListener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Animation: scale up then trigger callback
                    v.animate()
                            .scaleX(1.3f)
                            .scaleY(1.3f)
                            .setDuration(150)
                            .withEndAction(() -> {
                                v.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(150)
                                        .start();
                                markLearnedListener.onMarkLearned(word, currentPosition);
                            })
                            .start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivImage, btnMarkLearned;
        TextView tvWord, tvPhonetic, tvWordType, tvTopic, tvMeaning;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivImage = itemView.findViewById(R.id.ivFlashcardImage);
            btnMarkLearned = itemView.findViewById(R.id.btnMarkLearned);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvWordType = itemView.findViewById(R.id.tvWordType);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
        }
    }
}