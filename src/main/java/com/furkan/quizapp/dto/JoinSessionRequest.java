package com.furkan.quizapp.dto;

public record JoinSessionRequest(
    String sessionCode,
    String nickname
) {}
