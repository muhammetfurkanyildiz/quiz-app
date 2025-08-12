package com.furkan.quizapp.dto;

import java.util.List;

public record QuizAnswerRequest(
    Long quizId,
    List<DtoAnswer> answers
) {}