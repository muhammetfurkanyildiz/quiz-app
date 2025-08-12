package com.furkan.quizapp.dto;

public record JoinSessionResponse(
    Long participantId,
    String nickname,
    Long sessionId
) {}