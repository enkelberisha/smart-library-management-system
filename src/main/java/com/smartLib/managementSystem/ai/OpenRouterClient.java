package com.smartLib.managementSystem.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Component
public class OpenRouterClient {

    @Value("${OPENROUTER_API_KEY:}")
    private String apiKey;

    private static final String URL = "https://openrouter.ai/api/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String systemPrompt, String userPrompt) {

        String key = (apiKey == null) ? "" : apiKey.trim();

        if (key.isBlank()) {
            throw new IllegalStateException("OPENROUTER_API_KEY is missing. Add it to .env and pass it into docker-compose.");
        }

        System.out.println("🔑 OpenRouter key prefix: " + key.substring(0, Math.min(8, key.length())) + "...");

        Map<String, Object> body = Map.of(
                "model", "allenai/olmo-3.1-32b-think:free",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", "Bearer " + key);

        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "SmartLibrary");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, request, Map.class);

            Map<?, ?> bodyMap = response.getBody();
            if (bodyMap == null) throw new IllegalStateException("OpenRouter returned empty response body.");

            List<?> choices = (List<?>) bodyMap.get("choices");
            if (choices == null || choices.isEmpty()) throw new IllegalStateException("OpenRouter returned no choices: " + bodyMap);

            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) choice.get("message");

            return message.get("content").toString();

        } catch (HttpClientErrorException e) {

            throw new IllegalStateException("OpenRouter HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }
}
