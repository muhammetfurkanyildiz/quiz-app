package com.furkan.quizapp.dto;

public record CreateSessionResponse(
    Long sessionId,
    String sessionCode,
    Long quizId,
    String category
) {}
