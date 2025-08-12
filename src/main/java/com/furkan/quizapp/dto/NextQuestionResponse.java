package com.furkan.quizapp.dto;


import java.util.List;

public record NextQuestionResponse(
    Long questionId,
    String questionText,
    List<String> options,
    int currentIndex,
    int totalQuestions,
    int timeLimitSeconds
) {}
