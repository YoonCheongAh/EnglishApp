package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.model.Flashcard;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${pexels.api.key:}")
    private String pexelsApiKey;

    @Value("${voicerss.api.key}")
    private String voiceRssApiKey;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final String PEXELS_API_URL = "https://api.pexels.com/v1/search";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Flashcard> generateFlashcardsFromImage(MultipartFile file) throws Exception {
        validateGeminiApiKey();

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType();

        String prompt = buildImagePrompt();
        String response = callGeminiWithImage(base64Image, mimeType, prompt);

        return parseFlashcardsWithImages(response);
    }

    public List<Flashcard> generateFlashcardsFromWords(List<String> words) throws Exception {
        validateGeminiApiKey();

        String prompt = buildWordsPrompt(words);
        String response = callGeminiWithText(prompt);

        return parseFlashcardsWithImages(response);
    }

    private void validateGeminiApiKey() {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key not configured");
        }
    }

    private String buildImagePrompt() {
        return """
            Analyze this image and identify all visible objects, animals, or things.
            
            For each identified item, create a flashcard with these exact fields:
            - word: the English word
            - wordType: part of speech (noun, verb, adjective, etc.)
            - phonetic: IPA pronunciation
            - meaningEn: English definition
            - meaningVn: Vietnamese translation
            
            IMPORTANT: Return ONLY a valid JSON array with no markdown formatting, no explanations, no code blocks.
            Example format:
            [
              {
                "word": "cat",
                "wordType": "noun",
                "phonetic": "/kæt/",
                "meaningEn": "a small domesticated carnivorous mammal",
                "meaningVn": "con mèo"
              }
            ]
            """;
    }

    private String buildWordsPrompt(List<String> words) {
        return String.format("""
            Create flashcards for these English words: %s
            
            For each word, provide these exact fields:
            - word: the English word
            - wordType: part of speech (noun, verb, adjective, etc.)
            - phonetic: IPA pronunciation
            - meaningEn: English definition
            - meaningVn: Vietnamese translation
            
            IMPORTANT: Return ONLY a valid JSON array with no markdown formatting, no explanations, no code blocks.
            Example format:
            [
              {
                "word": "cat",
                "wordType": "noun",
                "phonetic": "/kæt/",
                "meaningEn": "a small domesticated carnivorous mammal",
                "meaningVn": "con mèo"
              }
            ]
            """, String.join(", ", words));
    }

    private String callGeminiWithImage(String base64Image, String mimeType, String prompt)
            throws Exception {

        String url = GEMINI_API_URL + "?key=" + geminiApiKey;

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();

        parts.add(Map.of("text", prompt));
        parts.add(Map.of("inline_data", Map.of(
                "mime_type", mimeType,
                "data", base64Image
        )));

        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        requestBody.put("generationConfig", Map.of(
                "temperature", 0.3,
                "maxOutputTokens", 2048,
                "responseMimeType", "application/json"
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class
        );

        return extractTextFromResponse(response.getBody());
    }

    private String callGeminiWithText(String prompt) throws Exception {

        String url = GEMINI_API_URL + "?key=" + geminiApiKey;

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        contents.add(Map.of("parts", List.of(Map.of("text", prompt))));
        requestBody.put("contents", contents);

        requestBody.put("generationConfig", Map.of(
                "temperature", 0.3,
                "maxOutputTokens", 2048,
                "responseMimeType", "application/json"
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class
        );

        return extractTextFromResponse(response.getBody());
    }

    private String extractTextFromResponse(String responseBody) throws Exception {

        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode textNode = root
                .path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text");

        if (!textNode.isMissingNode()) {
            return textNode.asText();
        }

        throw new RuntimeException("Invalid Gemini response format");
    }

    private List<Flashcard> parseFlashcardsWithImages(String jsonResponse) throws Exception {
        // Làm sạch response (loại bỏ markdown nếu có)
        String cleaned = jsonResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();

        // Tìm JSON array
        int start = cleaned.indexOf("[");
        int end = cleaned.lastIndexOf("]") + 1;

        if (start < 0 || end <= start) {
            // Log để debug
            System.err.println("Raw Gemini response: " + jsonResponse);
            throw new RuntimeException("AI response is not a valid JSON array. Response: " +
                    jsonResponse.substring(0, Math.min(200, jsonResponse.length())));
        }

        cleaned = cleaned.substring(start, end);

        List<Map<String, String>> rawCards =
                objectMapper.readValue(cleaned, new TypeReference<>() {});

        List<Flashcard> flashcards = new ArrayList<>();

        for (Map<String, String> raw : rawCards) {
            Flashcard card = new Flashcard();

            String word = raw.getOrDefault("word", "");

            card.setWord(word);
            card.setWordType(raw.getOrDefault("wordType", ""));
            card.setPhonetic(raw.getOrDefault("phonetic", ""));
            card.setMeaningEn(raw.getOrDefault("meaningEn", ""));
            card.setMeaningVN(raw.getOrDefault("meaningVn", ""));
            // Đã xóa: card.setTopic(...) - không set topic nữa

            if (isPexelsConfigured()) {
                card.setImageUrl(fetchImageFromPexels(word));
            } else {
                card.setImageUrl(null);
            }

            card.setAudioUrl(generateAudioUrl(word));

            flashcards.add(card);
        }

        return flashcards;
    }

    private String fetchImageFromPexels(String word) {
        try {
            String url = PEXELS_API_URL + "?query=" + URLEncoder.encode(word, StandardCharsets.UTF_8) + "&per_page=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", pexelsApiKey);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode photos = root.get("photos");

            if (photos != null && photos.size() > 0) {
                return photos.get(0).get("src").get("large").asText();
            }
        } catch (Exception ignored) {}

        return null;
    }

    public boolean isGeminiConfigured() {
        return geminiApiKey != null && !geminiApiKey.isEmpty();
    }

    public boolean isPexelsConfigured() {
        return pexelsApiKey != null && !pexelsApiKey.isEmpty();
    }

    public Map<String, String> getApiStatus() {
        return Map.of(
                "gemini", isGeminiConfigured() ? "configured" : "not_configured",
                "pexels", isPexelsConfigured() ? "configured" : "not_configured"
        );
    }

    private String generateAudioUrl(String word) {
        try {
            return "https://api.voicerss.org/?key=" + voiceRssApiKey
                    + "&hl=en-us&v=Linda&src="
                    + URLEncoder.encode(word, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}