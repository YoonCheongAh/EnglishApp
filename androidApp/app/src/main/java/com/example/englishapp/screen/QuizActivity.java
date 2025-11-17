package com.example.englishapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.api.QuizApi;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.model.QuizQuestion;
import com.example.englishapp.model.QuizResponse;
import com.example.englishapp.model.QuizResultResponse;
import com.example.englishapp.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvWord, tvWordType;
    private ImageView ivWordImage;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext, btnSubmit;
    private ProgressBar progressBar;

    private QuizApi quizApi;
    private SessionManager sessionManager;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        sessionManager = new SessionManager(this);
        quizApi = RetrofitClient.getInstance().create(QuizApi.class);

        loadQuiz();
    }

    private void initViews() {
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvWord = findViewById(R.id.tv_word);
        tvWordType = findViewById(R.id.tv_word_type);
        ivWordImage = findViewById(R.id.iv_word_image);
        rgOptions = findViewById(R.id.rg_options);
        rbOption1 = findViewById(R.id.rb_option1);
        rbOption2 = findViewById(R.id.rb_option2);
        rbOption3 = findViewById(R.id.rb_option3);
        rbOption4 = findViewById(R.id.rb_option4);
        btnNext = findViewById(R.id.btn_next);
        btnSubmit = findViewById(R.id.btn_submit);
        progressBar = findViewById(R.id.progress_bar);

        btnNext.setOnClickListener(v -> nextQuestion());
        btnSubmit.setOnClickListener(v -> submitQuiz());
    }

    private void loadQuiz() {
        progressBar.setVisibility(View.VISIBLE);

        Long userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId.intValue());
        body.put("numberOfQuestions", 10);

        quizApi.generateQuiz(body).enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    QuizResponse quizResponse = response.body();
                    questions = quizResponse.getQuestions();

                    if (questions == null || questions.isEmpty()) {
                        Toast.makeText(QuizActivity.this,
                                quizResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    displayQuestion(0);
                } else {
                    Toast.makeText(QuizActivity.this, "Không thể tải quiz", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(QuizActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayQuestion(int index) {
        if (index >= questions.size()) {
            submitQuiz();
            return;
        }

        currentQuestionIndex = index;
        QuizQuestion question = questions.get(index);

        // Update UI
        tvQuestionNumber.setText("Câu " + (index + 1) + "/" + questions.size());
        tvWord.setText(question.getWord());
        tvWordType.setText(question.getWordType());

        // Load image
        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(question.getImageUrl())
                    .placeholder(R.drawable.default_avatar)
                    .into(ivWordImage);
            ivWordImage.setVisibility(View.VISIBLE);
        } else {
            ivWordImage.setVisibility(View.GONE);
        }

        // Set options
        List<String> options = question.getOptions();
        if (options != null && options.size() == 4) {
            rbOption1.setText(options.get(0));
            rbOption2.setText(options.get(1));
            rbOption3.setText(options.get(2));
            rbOption4.setText(options.get(3));
        }

        // Clear previous selection
        rgOptions.clearCheck();

        // Show/hide buttons
        if (index == questions.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }
    }

    private void nextQuestion() {
        // Save answer
        saveCurrentAnswer();

        // Next question
        currentQuestionIndex++;
        displayQuestion(currentQuestionIndex);
    }

    private void saveCurrentAnswer() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selected = findViewById(selectedId);
            String answer = selected.getText().toString();
            questions.get(currentQuestionIndex).setSelectedAnswer(answer);
        }
    }

    private void submitQuiz() {
        // Save last answer
        saveCurrentAnswer();

        // Check if all answered
        for (QuizQuestion q : questions) {
            if (q.getSelectedAnswer() == null) {
                Toast.makeText(this, "Vui lòng trả lời tất cả câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);

        // Prepare answers
        Map<Integer, String> answers = new HashMap<>();
        for (QuizQuestion q : questions) {
            answers.put(q.getDailyWordId(), q.getSelectedAnswer());
        }

        Long userId = sessionManager.getUserId();
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId.intValue());
        body.put("answers", answers);

        quizApi.submitQuiz(body).enqueue(new Callback<QuizResultResponse>() {
            @Override
            public void onResponse(Call<QuizResultResponse> call, Response<QuizResultResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    QuizResultResponse result = response.body();

                    // Navigate to result screen
                    Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
                    intent.putExtra("correctCount", result.getCorrectCount());
                    intent.putExtra("totalQuestions", result.getTotalQuestions());
                    intent.putExtra("score", result.getScore());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(QuizActivity.this, "Không thể submit quiz", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuizResultResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(QuizActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
