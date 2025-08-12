package com.furkan.quizapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.furkan.quizapp.dto.CreateSessionResponse;

public interface IRestQuizSessionController {
    public ResponseEntity<CreateSessionResponse> createSession(Long quizId,String category);
    public ResponseEntity<String> startSession(@PathVariable String sessionCode);
    @PostMapping("/force-end/{sessionCode}")
    public ResponseEntity<Void> forceEndSession(@PathVariable String sessionCode);

}
