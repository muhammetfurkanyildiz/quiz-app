package com.furkan.quizapp.dto;

import java.time.LocalDateTime;

public record ParticipantResult(
    String nickname,
    int score,
    LocalDateTime submittedAt
) {}

