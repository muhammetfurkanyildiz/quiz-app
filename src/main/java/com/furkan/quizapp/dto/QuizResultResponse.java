package com.furkan.quizapp.dto;


public record QuizResultResponse(
    int totalQuestions,
    int correctAnswers,
    int score // 10 üzerinden
) {}
