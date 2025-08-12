package com.furkan.quizapp.dto;

public record EmptyAnswerRequest(
    String sessionCode,
    String nickname
) {}