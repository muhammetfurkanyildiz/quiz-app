package com.furkan.quizapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.furkan.quizapp.controller.IRestQuizController;
import com.furkan.quizapp.dto.QuizAnswerRequest;
import com.furkan.quizapp.dto.QuizRequest;
import com.furkan.quizapp.dto.QuizResponse;
import com.furkan.quizapp.dto.QuizResultResponse;
import com.furkan.quizapp.service.IQuizService;
@Controller
public class RestQuizControllerImpl implements IRestQuizController {
    @Autowired
    private IQuizService quizService;

    @PostMapping("/start")
    @Override
    public ResponseEntity<QuizResponse> startQuiz(
            @RequestBody QuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        System.out.println("Starting quiz for user: " + email + " with category: " + request.getCategory());
        
        QuizResponse response = quizService.startQuiz(request.getCategory(), email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    @Override
    public ResponseEntity<QuizResultResponse> submitQuiz(QuizAnswerRequest request) {
        QuizResultResponse result = quizService.evaluateQuiz(request);
        return ResponseEntity.ok(result);
    
    }
}
