package com.furkan.quizapp.dto;

import java.time.LocalDateTime;

public record ParticipantInfo(
    Long id,
    String nickname,
    LocalDateTime submittedAt,
    int score
) {}
