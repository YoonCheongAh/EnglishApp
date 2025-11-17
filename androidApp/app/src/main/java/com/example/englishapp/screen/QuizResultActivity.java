package com.example.englishapp.screen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.englishapp.R;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvScore, tvResult, tvMessage;
    private Button btnRetry, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        initViews();
        displayResult();
    }

    private void initViews() {
        tvScore = findViewById(R.id.tv_score);
        tvResult = findViewById(R.id.tv_result);
        tvMessage = findViewById(R.id.tv_message);
        btnRetry = findViewById(R.id.btn_retry);
        btnBack = findViewById(R.id.btn_back);

        btnRetry.setOnClickListener(v -> {
            finish();
            // Restart QuizActivity
            startActivity(getIntent().getClass().equals(QuizResultActivity.class)
                    ? getIntent()
                    : new android.content.Intent(this, QuizActivity.class));
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void displayResult() {
        int correctCount = getIntent().getIntExtra("correctCount", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        double score = getIntent().getDoubleExtra("score", 0);

        tvScore.setText(String.format("%.0f%%", score));
        tvResult.setText(correctCount + "/" + totalQuestions + " câu đúng");

        // Message based on score
        String message;
        if (score >= 90) {
            message = "Xuất sắc! Bạn làm rất tốt!";
        } else if (score >= 70) {
            message = "Tốt lắm! Tiếp tục phát huy!";
        } else if (score >= 50) {
            message = "Khá ổn! Hãy cố gắng hơn nữa!";
        } else {
            message = "Hãy ôn tập thêm nhé!";
        }

        tvMessage.setText(message);
    }
}
