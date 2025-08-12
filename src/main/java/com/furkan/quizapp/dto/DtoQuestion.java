package com.furkan.quizapp.dto;

import java.util.List;

public record DtoQuestion(
    Long id,
    String questionText,
    List<DtoOption> options
) {}
