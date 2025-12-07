package com.example.englishapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.englishapp.R;
import com.google.android.material.button.MaterialButton;

public class ActivitiesFragment extends Fragment {

    private MaterialButton quizButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        quizButton = view.findViewById(R.id.quiz_button_play);

        quizButton.setOnClickListener(v -> openQuiz());

        return view;
    }

    private void openQuiz() {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        startActivity(intent);
    }
}