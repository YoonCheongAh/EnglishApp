package com.example.englishapp.screen;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;

public class ActivitiesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Xử lý nút Quiz
        view.findViewById(R.id.quiz_button).setOnClickListener(v -> {
            // Mở Quiz activity
        });

        // Xử lý nút Sliding Puzzle
        view.findViewById(R.id.puzzle_button).setOnClickListener(v -> {
            // Mở Puzzle activity
        });
    }
}