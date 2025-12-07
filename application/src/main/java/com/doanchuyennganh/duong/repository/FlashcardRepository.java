package com.doanchuyennganh.duong.repository;

import com.doanchuyennganh.duong.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    @Query("SELECT f FROM Flashcard f WHERE f.topicEntity.topicId = :topicId")
    List<Flashcard> findByTopicId(@Param("topicId") Long topicId);
    List<Flashcard> findByWordContainingIgnoreCase(String keyword);

    @Query(value = "SELECT * FROM flashcards ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Flashcard> findRandomFlashcards(Integer limit);

    @Query(value = "SELECT * FROM flashcards WHERE flashcard_id NOT IN " +
            "(SELECT flashcard_id FROM daily_words WHERE user_id = :userId) " +
            "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Flashcard> findNewFlashcardsForUser(Integer userId, Integer limit);

}
