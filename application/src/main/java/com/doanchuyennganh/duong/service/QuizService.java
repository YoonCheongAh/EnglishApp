package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.dto.QuizQuestion;
import com.doanchuyennganh.duong.dto.QuizResponse;
import com.doanchuyennganh.duong.dto.QuizResultResponse;
import com.doanchuyennganh.duong.model.DailyWord;
import com.doanchuyennganh.duong.model.Flashcard;
import com.doanchuyennganh.duong.repository.DailyWordRepository;
import com.doanchuyennganh.duong.repository.FlashcardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final DailyWordRepository dailyWordRepository;
    private final FlashcardRepository flashcardRepository;

    public QuizService(DailyWordRepository dailyWordRepository,
                       FlashcardRepository flashcardRepository) {
        this.dailyWordRepository = dailyWordRepository;
        this.flashcardRepository = flashcardRepository;
    }

    /**
     * Tạo quiz từ các từ đã học
     */
    @Transactional(readOnly = true)
    public QuizResponse generateQuiz(Integer userId, Integer numberOfQuestions) {
        List<DailyWord> learnedWords = dailyWordRepository.findByUserIdAndIsLearned(userId, true);

        QuizResponse response = new QuizResponse();

        if (learnedWords.isEmpty()) {
            response.setMessage("Bạn chưa học từ nào. Hãy học từ trước khi làm quiz!");
            response.setQuestions(new ArrayList<>());
            response.setTotalQuestions(0);
            return response;
        }

        Collections.shuffle(learnedWords);
        int actualQuestions = Math.min(numberOfQuestions, learnedWords.size());
        List<DailyWord> selectedWords = learnedWords.subList(0, actualQuestions);

        List<QuizQuestion> questions = new ArrayList<>();
        for (DailyWord dailyWord : selectedWords) {
            Flashcard flashcard = flashcardRepository.findById(dailyWord.getFlashcardId().longValue())
                    .orElse(null);

            if (flashcard != null) {
                QuizQuestion question = createQuestion(dailyWord, flashcard, learnedWords);
                questions.add(question);
            }
        }

        response.setQuestions(questions);
        response.setTotalQuestions(questions.size());
        response.setMessage("Quiz generated successfully");

        return response;
    }

    /**
     * Tạo một câu hỏi với 4 options (1 đúng + 3 sai)
     * Dùng meaning_en làm đáp án
     */
    private QuizQuestion createQuestion(DailyWord dailyWord, Flashcard correctFlashcard,
                                        List<DailyWord> allWords) {
        QuizQuestion question = new QuizQuestion();
        question.setDailyWordId(dailyWord.getDailyWordId());
        question.setWord(correctFlashcard.getWord()); // hiển thị từ gốc
        question.setCorrectAnswer(correctFlashcard.getMeaningEn()); // đáp án tiếng Anh
        question.setImageUrl(correctFlashcard.getImageUrl());
        question.setAudioUrl(correctFlashcard.getAudioUrl());
        question.setWordType(correctFlashcard.getWordType());

        // Tạo 3 đáp án sai từ các từ khác
        List<String> wrongAnswers = allWords.stream()
                .filter(dw -> !dw.getDailyWordId().equals(dailyWord.getDailyWordId()))
                .map(dw -> {
                    Flashcard fc = flashcardRepository.findById(dw.getFlashcardId().longValue())
                            .orElse(null);
                    return fc != null ? fc.getMeaningEn() : null; // lấy meaning_en
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Collections.shuffle(wrongAnswers);
        List<String> selectedWrong = wrongAnswers.stream()
                .limit(3)
                .collect(Collectors.toList());

        List<String> options = new ArrayList<>(selectedWrong);
        options.add(correctFlashcard.getMeaningEn());
        Collections.shuffle(options);

        question.setOptions(options);
        return question;
    }

    /**
     * Chấm điểm quiz (so sánh bằng meaning_en)
     */
    @Transactional(readOnly = true)
    public QuizResultResponse submitQuiz(Integer userId, Map<Integer, String> userAnswers) {
        QuizResultResponse response = new QuizResultResponse();
        List<QuizResultResponse.QuestionResult> results = new ArrayList<>();

        int correctCount = 0;

        for (Map.Entry<Integer, String> entry : userAnswers.entrySet()) {
            Integer dailyWordId = entry.getKey();
            String userAnswer = entry.getValue();

            DailyWord dailyWord = dailyWordRepository.findById(dailyWordId).orElse(null);
            if (dailyWord == null) continue;

            Flashcard flashcard = flashcardRepository.findById(dailyWord.getFlashcardId().longValue())
                    .orElse(null);
            if (flashcard == null) continue;

            String correctAnswer = flashcard.getMeaningEn(); // dùng meaning_en
            boolean isCorrect = correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());

            if (isCorrect) correctCount++;

            QuizResultResponse.QuestionResult result = new QuizResultResponse.QuestionResult();
            result.setDailyWordId(dailyWordId);
            result.setWord(flashcard.getWord());
            result.setUserAnswer(userAnswer);
            result.setCorrectAnswer(correctAnswer);
            result.setCorrect(isCorrect);

            results.add(result);
        }

        int totalQuestions = userAnswers.size();
        double score = totalQuestions > 0 ? (correctCount * 100.0 / totalQuestions) : 0;

        response.setCorrectCount(correctCount);
        response.setTotalQuestions(totalQuestions);
        response.setScore(Math.round(score * 100.0) / 100.0);
        response.setResults(results);

        return response;
    }
}
