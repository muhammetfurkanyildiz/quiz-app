package com.furkan.quizapp.dto;

public record DtoAnswer(
    Long questionId,
    String selectedOptionText
) {}