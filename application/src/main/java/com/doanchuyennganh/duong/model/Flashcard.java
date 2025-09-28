package com.doanchuyennganh.duong.model;
import jakarta.persistence.*;

@Entity
@Table(name = "flashcards")
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flashcard_id")
    private Long id;

    @Column(name = "word")
    private String word;

    @Column(name = "word_type")
    private String wordType;

    @Column(name = "topic")
    private String topic;

    @Column(name = "phonetic")
    private String phonetic;

    @Column(name = "meaning_en", columnDefinition = "TEXT")
    private String meaningEn;

    @Column(name = "meaning_vn", columnDefinition = "TEXT")
    private String meaningVN;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name= "audio_url", columnDefinition = "TEXT")
    private String audioUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getMeaningEn() {
        return meaningEn;
    }

    public void setMeaningEn(String meaningEn) {
        this.meaningEn = meaningEn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMeaningVN() {
        return meaningVN;
    }

    public void setMeaningVN(String meaningVN) {
        this.meaningVN = meaningVN;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}

