package com.furkan.quizapp.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furkan.quizapp.dto.DtoOption;
import com.furkan.quizapp.dto.DtoQuestion;
import com.furkan.quizapp.service.IGptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GptServiceImpl implements IGptService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public List<DtoQuestion> generateQuestions(String category) {
        String prompt = String.format("""
            Category: %s
            Generate 10 multiple-choice questions.
            Each question must have 2 to 4 options.
            Respond ONLY with raw JSON. Do NOT include any explanation or introductory text.
            Format JSON like this:
            [
              {
                "questionText": "...",
                "options": [
                  {"text": "...", "isCorrect": true/false},
                  ...
                ]
              },
              ...
            ]
            """, category);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root
                    .get("choices").get(0)
                    .get("message")
                    .get("content")
                    .asText();

            // DEBUG: Yanıtı görmek için logla
            System.out.println("GPT'den gelen yanıt:\n" + content);

            // Yanıtın sadece JSON kısmını al
            int jsonStart = content.indexOf('[');
            if (jsonStart == -1) {
                throw new RuntimeException("JSON içeriği bulunamadı. Yanıt:\n" + content);
            }
            String jsonOnly = content.substring(jsonStart);

            JsonNode quizArray = objectMapper.readTree(jsonOnly);

            List<DtoQuestion> questionList = new ArrayList<>();

            for (JsonNode qNode : quizArray) {
                String questionText = qNode.get("questionText").asText();
                List<DtoOption> options = new ArrayList<>();
                for (JsonNode opt : qNode.get("options")) {
                    options.add(new DtoOption(
                            opt.get("text").asText(),
                            opt.get("isCorrect").asBoolean()
                    ));
                }
                questionList.add(new DtoQuestion(null,questionText, options));
            }

            return questionList;

        } catch (Exception e) {
            throw new RuntimeException("GPT yanıtı işlenemedi: " + e.getMessage(), e);
        }
    }
}
