package com.doanchuyennganh.duong.repository;

import com.doanchuyennganh.duong.model.DailyWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyWordRepository extends JpaRepository<DailyWord, Integer> {

    /**
     * Lấy danh sách từ của user trong một ngày cụ thể
     */
    List<DailyWord> findByUserIdAndWordDate(Integer userId, LocalDate wordDate);

    /**
     * Lấy tất cả từ đã học của user (từ tất cả các ngày)
     */
    List<DailyWord> findByUserIdAndIsLearned(Integer userId, Boolean isLearned);

    /**
     * Đếm số từ đã học của user trong một ngày
     */
    @Query("SELECT COUNT(dw) FROM DailyWord dw WHERE dw.userId = :userId AND dw.wordDate = :date AND dw.isLearned = true")
    Integer countLearnedWordsByUserAndDate(Integer userId, LocalDate date);

    /**
     * Đếm tổng số từ của user trong một ngày
     */
    @Query("SELECT COUNT(dw) FROM DailyWord dw WHERE dw.userId = :userId AND dw.wordDate = :date")
    Integer countTotalWordsByUserAndDate(Integer userId, LocalDate date);

    /**
     * Kiểm tra xem user đã có từ trong ngày chưa
     */
    boolean existsByUserIdAndWordDate(Integer userId, LocalDate wordDate);

    /**
     * Kiểm tra duplicate: user đã có flashcard này trong ngày chưa
     */
    boolean existsByUserIdAndWordDateAndFlashcardId(Integer userId, LocalDate wordDate, Integer flashcardId);

    /**
     * Lấy danh sách từ của user trong khoảng thời gian
     */
    List<DailyWord> findByUserIdAndWordDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    /**
     * Xóa tất cả từ của user trong một ngày (dùng cho refresh)
     */
    void deleteByUserIdAndWordDate(Integer userId, LocalDate wordDate);
}