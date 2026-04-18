package com.ek.app.mcp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LLMServiceImpl implements LLMService {

    private static final String SYSTEM_CHAT_PROMPT = "You are a helpful assistant.";
    private static final String SYSTEM_STRUCTURED_PROMPT = "Return only valid JSON object. No markdown, no extra text.";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String chatCompletionsUrl;
    private final String model;

    public LLMServiceImpl(
            ObjectMapper objectMapper,
            @Value("${openai.api.key:${OPENAI_API_KEY:}}") String apiKey,
            @Value("${openai.api.chat-completions-url:https://api.openai.com/v1/chat/completions}") String chatCompletionsUrl,
            @Value("${openai.api.model:gpt-4o-mini}") String model) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.chatCompletionsUrl = chatCompletionsUrl;
        this.model = model;
    }

    @Override
    public String chat(String prompt) {
        String normalizedPrompt = prompt == null ? "" : prompt.trim();
        if (normalizedPrompt.isEmpty()) {
            return "Please provide a prompt.";
        }

        return callOpenAI(
                List.of(
                        Map.of("role", "system", "content", SYSTEM_CHAT_PROMPT),
                        Map.of("role", "user", "content", normalizedPrompt)
                ),
                false
        );
    }

    @Override
    public Map<String, Object> structuredDataResponse(String prompt) {
        String normalizedPrompt = prompt == null ? "" : prompt.trim();
        if (normalizedPrompt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prompt is required");
        }

        String rawJson = callOpenAI(
                List.of(
                        Map.of("role", "system", "content", SYSTEM_STRUCTURED_PROMPT),
                        Map.of("role", "user", "content", normalizedPrompt)
                ),
                true
        );

        try {
            return objectMapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException ex) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("rawResponse", rawJson);
            fallback.put("parseError", "Model response was not valid JSON");
            return fallback;
        }
    }

    private String callOpenAI(List<Map<String, Object>> messages, boolean structuredJson) {
        if (apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "OpenAI API key missing. Set OPENAI_API_KEY or openai.api.key");
        }

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);
            payload.put("messages", messages);
            payload.put("temperature", 0.2);
            if (structuredJson) {
                payload.put("response_format", Map.of("type", "json_object"));
            }

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(chatCompletionsUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "OpenAI API error: " + response.statusCode() + " " + response.body());
            }

            Map<String, Object> responseJson = objectMapper.readValue(
                    response.body(),
                    new TypeReference<Map<String, Object>>() {
                    }
            );

            Object choicesObj = responseJson.get("choices");
            if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI API returned no choices");
            }

            Object firstChoice = choices.get(0);
            if (!(firstChoice instanceof Map<?, ?> firstChoiceMap)) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unexpected OpenAI response format");
            }

            Object messageObj = firstChoiceMap.get("message");
            if (!(messageObj instanceof Map<?, ?> messageMap)) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI response missing message");
            }

            Object contentObj = messageMap.get("content");
            if (!(contentObj instanceof String content) || content.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI response content is empty");
            }

            return content.trim();
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed calling OpenAI service", ex);
        }
    }
}
