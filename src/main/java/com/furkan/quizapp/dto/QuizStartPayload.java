package com.furkan.quizapp.dto;

import java.util.List;



public record QuizStartPayload(
        String sessionCode,
        Long questionId,
        String questionText,
        List<String> options, // sadece metinleri gönderiyoruz
        int questionIndex,
        int totalQuestions,
        int timeLimitSeconds
) {}
