package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.dto.DailyWordDTO;
import com.doanchuyennganh.duong.exception.CustomException;
import com.doanchuyennganh.duong.model.DailyWord;
import com.doanchuyennganh.duong.model.Flashcard;
import com.doanchuyennganh.duong.repository.DailyWordRepository;
import com.doanchuyennganh.duong.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyWordService {

    private final DailyWordRepository dailyWordRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserSettingsService settingsService;

    @Autowired
    @Lazy
    private DailyWordService self;

    public DailyWordService(DailyWordRepository dailyWordRepository,
                            FlashcardRepository flashcardRepository,
                            UserSettingsService settingsService) {
        this.dailyWordRepository = dailyWordRepository;
        this.flashcardRepository = flashcardRepository;
        this.settingsService = settingsService;
    }

    /** Tạo từ mới cho ngày hôm nay dựa trên daily goal */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DailyWordDTO> generateTodayWords(Integer userId) {
        LocalDate today = LocalDate.now();

        // Kiểm tra đã có từ hôm nay chưa
        List<DailyWord> existing = dailyWordRepository.findByUserIdAndWordDate(userId, today);
        if (!existing.isEmpty()) {
            return existing.stream()
                    .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                    .collect(Collectors.toList());
        }

        Integer dailyGoal = settingsService.getDailyGoal(userId);

        // Lấy flashcard mới chưa học (có thể request nhiều hơn để có buffer)
        int requestLimit = Math.min(dailyGoal * 2, 100); // Request gấp đôi để đảm bảo đủ unique
        List<Flashcard> newFlashcards = flashcardRepository.findNewFlashcardsForUser(userId, requestLimit);

        // Nếu chưa đủ, lấy thêm random
        if (newFlashcards.size() < dailyGoal) {
            int remain = dailyGoal - newFlashcards.size();
            List<Flashcard> randomCards = flashcardRepository.findRandomFlashcards(remain * 2);
            newFlashcards.addAll(randomCards);
        }

        // CRITICAL: Remove duplicates based on flashcard ID using LinkedHashMap
        // LinkedHashMap preserves insertion order
        Map<Long, Flashcard> uniqueFlashcards = new LinkedHashMap<>();
        for (Flashcard fc : newFlashcards) {
            uniqueFlashcards.putIfAbsent(fc.getId(), fc);
        }

        // Lấy unique flashcards, limit theo dailyGoal
        List<Flashcard> finalFlashcards = uniqueFlashcards.values().stream()
                .limit(dailyGoal)
                .collect(Collectors.toList());

        // Create DailyWord entities
        List<DailyWord> entities = new ArrayList<>();
        for (Flashcard fc : finalFlashcards) {
            DailyWord dw = new DailyWord();
            dw.setUserId(userId);
            dw.setFlashcardId(fc.getId().intValue());
            dw.setWordDate(today);
            dw.setIsLearned(false);
            entities.add(dw);
        }

        if (entities.isEmpty()) {
            return new ArrayList<>();
        }

        // Save all at once
        entities = dailyWordRepository.saveAll(entities);

        return entities.stream()
                .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                .collect(Collectors.toList());
    }

    /** Lấy danh sách từ hôm nay, nếu chưa có thì tạo tự động */
    @Transactional(readOnly = true)
    public List<DailyWordDTO> getTodayWords(Integer userId) {
        LocalDate today = LocalDate.now();
        List<DailyWord> list = dailyWordRepository.findByUserIdAndWordDate(userId, today);

        if (list.isEmpty()) {
            try {
                return self.generateTodayWords(userId);
            } catch (Exception e) {
                // Nếu bị lỗi, query lại
                list = dailyWordRepository.findByUserIdAndWordDate(userId, today);
                if (!list.isEmpty()) {
                    return list.stream()
                            .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                            .collect(Collectors.toList());
                }
                return new ArrayList<>();
            }
        }

        return list.stream()
                .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                .collect(Collectors.toList());
    }

    /** Lấy tất cả từ đã học của user (từ tất cả các ngày) */
    @Transactional(readOnly = true)
    public List<DailyWordDTO> getAllLearnedWords(Integer userId) {
        List<DailyWord> learnedWords = dailyWordRepository.findByUserIdAndIsLearned(userId, true);

        return learnedWords.stream()
                .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                .collect(Collectors.toList());
    }

    /** Đánh dấu một từ đã học */
    @Transactional
    public DailyWordDTO markAsLearned(Integer userId, Integer dailyWordId, Boolean isLearned) {
        DailyWord dw = dailyWordRepository.findById(dailyWordId)
                .orElseThrow(() -> new CustomException.ResourceNotFoundException("Daily word not found"));

        if (!dw.getUserId().equals(userId)) {
            throw new CustomException.DailyWordException("Unauthorized access");
        }

        dw.setIsLearned(isLearned);
        dw = dailyWordRepository.save(dw);

        return convertToDTO(dw, findFlashcard(dw.getFlashcardId()));
    }

    /** Đánh dấu nhiều từ cùng lúc */
    @Transactional
    public List<DailyWordDTO> markMultipleAsLearned(Integer userId, List<Integer> dailyWordIds, Boolean isLearned) {
        List<DailyWord> words = dailyWordRepository.findAllById(dailyWordIds);

        for (DailyWord dw : words) {
            if (!dw.getUserId().equals(userId)) {
                throw new CustomException.DailyWordException("Unauthorized access to daily word: " + dw.getDailyWordId());
            }
            dw.setIsLearned(isLearned);
        }

        words = dailyWordRepository.saveAll(words);

        return words.stream()
                .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                .collect(Collectors.toList());
    }

    /** Xóa từ của ngày hôm nay trong transaction riêng */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTodayWords(Integer userId) {
        LocalDate today = LocalDate.now();
        dailyWordRepository.deleteByUserIdAndWordDate(userId, today);
    }

    /** Làm mới từ hôm nay (xóa và tạo lại) */
    public List<DailyWordDTO> refreshTodayWords(Integer userId) {
        // Xóa trong transaction riêng
        self.deleteTodayWords(userId);

        // Tạo mới trong transaction riêng
        return self.generateTodayWords(userId);
    }

    /** Lấy thống kê học tập cho một ngày */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics(Integer userId, LocalDate date) {
        List<DailyWord> words = dailyWordRepository.findByUserIdAndWordDate(userId, date);

        long learnedCount = words.stream().filter(DailyWord::getIsLearned).count();
        int total = words.size();
        int progress = total == 0 ? 0 : (int) (learnedCount * 100 / total);

        Map<String, Object> stats = new HashMap<>();
        stats.put("date", date.toString());
        stats.put("totalWords", total);
        stats.put("learnedWords", learnedCount);
        stats.put("progress", progress);
        stats.put("words", words.stream()
                .map(dw -> convertToDTO(dw, findFlashcard(dw.getFlashcardId())))
                .collect(Collectors.toList()));

        return stats;
    }

    /** ===== Helper ===== */
    private Flashcard findFlashcard(Integer id) {
        return flashcardRepository.findById(id.longValue())
                .orElseThrow(() -> new CustomException.ResourceNotFoundException("Flashcard not found: " + id));
    }

    private DailyWordDTO convertToDTO(DailyWord dw, Flashcard fc) {
        DailyWordDTO dto = new DailyWordDTO();
        dto.setDailyWordId(dw.getDailyWordId());
        dto.setFlashcardId(fc.getId().intValue());
        dto.setWord(fc.getWord());
        dto.setWordType(fc.getWordType());
        dto.setTopic(fc.getTopic());
        dto.setPhonetic(fc.getPhonetic());
        dto.setMeaningEn(fc.getMeaningEn());
        dto.setMeaningVn(fc.getMeaningVN());
        dto.setImageUrl(fc.getImageUrl());
        dto.setAudioUrl(fc.getAudioUrl());
        dto.setWordDate(dw.getWordDate().toString());
        dto.setIsLearned(dw.getIsLearned());
        return dto;
    }
}