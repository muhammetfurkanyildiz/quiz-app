package com.furkan.quizapp.service;

import com.furkan.quizapp.dto.QuizAnswerRequest;
import com.furkan.quizapp.dto.QuizResponse;
import com.furkan.quizapp.dto.QuizResultResponse;

public interface IQuizService {
    QuizResponse startQuiz(String category, String username);
    QuizResultResponse evaluateQuiz(QuizAnswerRequest request);
}
