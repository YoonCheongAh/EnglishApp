package com.doanchuyennganh.duong.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_words",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "word_date", "flashcard_id"}))
public class DailyWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dailyWordId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "flashcard_id", nullable = false)
    private Integer flashcardId;

    @Column(name = "word_date", nullable = false)
    private LocalDate wordDate;

    @Column(name = "is_learned", nullable = false)
    private Boolean isLearned = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ====== GETTER & SETTER ======

    public Integer getDailyWordId() {
        return dailyWordId;
    }

    public void setDailyWordId(Integer dailyWordId) {
        this.dailyWordId = dailyWordId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(Integer flashcardId) {
        this.flashcardId = flashcardId;
    }

    public LocalDate getWordDate() {
        return wordDate;
    }

    public void setWordDate(LocalDate wordDate) {
        this.wordDate = wordDate;
    }

    public Boolean getIsLearned() {
        return isLearned;
    }

    public void setIsLearned(Boolean isLearned) {
        this.isLearned = isLearned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
