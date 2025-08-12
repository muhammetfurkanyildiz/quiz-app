package com.furkan.quizapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furkan.quizapp.controller.IRestQuizSessionController;
import com.furkan.quizapp.dto.CreateSessionResponse;
import com.furkan.quizapp.service.IQuizSessionService;

@RestController
@RequestMapping("/api/session")
public class RestQuizSessionControllerImpl implements IRestQuizSessionController {

    @Autowired
    private IQuizSessionService sessionService;
    
    @PostMapping("/create")
    @Override
    public ResponseEntity<CreateSessionResponse> createSession(@RequestParam(required = false) Long quizId,@RequestParam(required = false) String category) {
        boolean isQuizIdPresent = quizId != null;
    boolean isCategoryPresent = category != null && !category.isBlank();

    if (isQuizIdPresent == isCategoryPresent) {
        
        return ResponseEntity.badRequest().body(null); // veya özel hata
    }
        return ResponseEntity.ok(sessionService.createSession(quizId,category));
    }

    @PostMapping("/start/{sessionCode}")
    @Override
    public ResponseEntity<String> startSession(@PathVariable String sessionCode) {
        sessionService.startSession(sessionCode);
        return ResponseEntity.ok("Quiz session started successfully.");
    }

    @PostMapping("/force-end/{sessionCode}")
    @Override
public ResponseEntity<Void> forceEndSession(@PathVariable String sessionCode) {
    sessionService.forceEndSession(sessionCode);
    return ResponseEntity.ok().build();
}


}
