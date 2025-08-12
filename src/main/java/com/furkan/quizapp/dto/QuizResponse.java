package com.furkan.quizapp.dto;



import java.util.List;

public record QuizResponse(
    Long quizId,
    String category,
    List<DtoQuestion> questions
) {}
