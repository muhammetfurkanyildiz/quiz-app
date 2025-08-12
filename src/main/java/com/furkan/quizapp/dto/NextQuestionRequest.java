package com.furkan.quizapp.dto;


public record NextQuestionRequest(
    String sessionCode,
    String nickname
) {}

