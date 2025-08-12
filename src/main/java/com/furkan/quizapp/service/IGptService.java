package com.furkan.quizapp.service;

import java.util.List;

import com.furkan.quizapp.dto.DtoQuestion;

public interface IGptService {
    List<DtoQuestion> generateQuestions(String category);
}
