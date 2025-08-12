package com.furkan.quizapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

import com.furkan.quizapp.dto.QuizAnswerRequest;
import com.furkan.quizapp.dto.QuizRequest;
import com.furkan.quizapp.dto.QuizResponse;
import com.furkan.quizapp.dto.QuizResultResponse;

public interface IRestQuizController {
        ResponseEntity<QuizResponse> startQuiz(@RequestBody QuizRequest request, UserDetails userDetails);
        ResponseEntity<QuizResultResponse> submitQuiz(@RequestBody QuizAnswerRequest request);

}
