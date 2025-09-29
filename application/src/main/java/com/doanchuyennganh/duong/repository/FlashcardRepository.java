package com.doanchuyennganh.duong.repository;

import com.doanchuyennganh.duong.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    @Query("SELECT f FROM Flashcard f WHERE f.topicEntity.topicId = :topicId")
    List<Flashcard> findByTopicId(@Param("topicId") Long topicId);
}
