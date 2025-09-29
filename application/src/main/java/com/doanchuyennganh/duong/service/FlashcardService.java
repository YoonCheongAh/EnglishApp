package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.dto.FlashcardRequest;
import com.doanchuyennganh.duong.dto.FlashcardResponse;
import com.doanchuyennganh.duong.model.Flashcard;
import com.doanchuyennganh.duong.repository.FlashcardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardService {
    private final FlashcardRepository repository;

    public FlashcardService(FlashcardRepository repository) {
        this.repository = repository;
    }

    public List<FlashcardResponse> getAllFlashcards() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FlashcardResponse getFlashcardById(Long id) {
        Flashcard flashcard = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));
        return mapToResponse(flashcard);
    }

    public FlashcardResponse createFlashcard(FlashcardRequest request) {
        Flashcard flashcard = new Flashcard();
        flashcard.setWord(request.getWord());
        flashcard.setWordType(request.getWordType());
        flashcard.setTopic(request.getTopic());
        flashcard.setPhonetic(request.getPhonetic());
        flashcard.setMeaningEn(request.getMeaningEn());
        flashcard.setMeaningVN(request.getMeaningVN());
        flashcard.setImageUrl(request.getImageUrl());
        flashcard.setAudioUrl(request.getAudioUrl());

        Flashcard saved = repository.save(flashcard);
        return mapToResponse(saved);
    }

    public FlashcardResponse updateFlashcard(Long id, FlashcardRequest request) {
        Flashcard flashcard = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));

        flashcard.setWord(request.getWord());
        flashcard.setWordType(request.getWordType());
        flashcard.setTopic(request.getTopic());
        flashcard.setPhonetic(request.getPhonetic());
        flashcard.setMeaningEn(request.getMeaningEn());
        flashcard.setMeaningVN(request.getMeaningVN());
        flashcard.setImageUrl(request.getImageUrl());
        flashcard.setAudioUrl(request.getAudioUrl());

        Flashcard updated = repository.save(flashcard);
        return mapToResponse(updated);
    }

    public void deleteFlashcard(Long id) {
        repository.deleteById(id);
    }

    private FlashcardResponse mapToResponse(Flashcard flashcard) {
        FlashcardResponse res = new FlashcardResponse();
        res.setFlashcardId(flashcard.getId());
        res.setWord(flashcard.getWord());
        res.setWordType(flashcard.getWordType());
        res.setTopic(flashcard.getTopic());
        res.setPhonetic(flashcard.getPhonetic());
        res.setMeaningEn(flashcard.getMeaningEn());
        res.setMeaningVN(flashcard.getMeaningVN());
        res.setImageUrl(flashcard.getImageUrl());
        res.setAudioUrl(flashcard.getAudioUrl());
        return res;
    }
    public List<FlashcardResponse> getFlashcardsByTopic(Long topicId) {
        return repository.findByTopicId(topicId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}

