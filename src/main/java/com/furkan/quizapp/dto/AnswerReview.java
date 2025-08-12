package com.furkan.quizapp.dto;

public record AnswerReview(

    Long questionId,
    String sessionCode,
    String nickname,
    String questionText,
    String selectedText,
    String correctText
) {}
