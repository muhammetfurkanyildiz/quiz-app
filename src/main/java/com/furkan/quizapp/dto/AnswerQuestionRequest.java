package com.furkan.quizapp.dto;

public record AnswerQuestionRequest(
    Long sessionId,
    Long questionId,
    String selectedText,
    String nickname // opsiyonel, sadece anonim kullanıcılar için
) {}
