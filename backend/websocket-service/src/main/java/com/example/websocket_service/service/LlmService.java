package com.example.websocket_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${llm.api.key}") // Adaugă :no-key ca default
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.model}")
    private String model;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateAIResponse(String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content",
                    "You are a helpful assistant for an energy management system. If the request is too complex, an admin might come through in the same chat."));
            messages.add(Map.of("role", "user", "content", userMessage));

            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Eroare AI: " + e.getMessage();
        }
        return "Nu am putut genera un răspuns.";
    }
}