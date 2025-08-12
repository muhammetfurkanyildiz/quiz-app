package com.furkan.quizapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.furkan.quizapp.service.IQuizSessionService;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private IQuizSessionService quizSessionService;

    @MessageMapping("/quiz/{sessionCode}/start")
    @SendTo("/topic/quiz/{sessionCode}")
    public String startQuiz(@DestinationVariable String sessionCode) {
        try {
            quizSessionService.startSession(sessionCode);
            return "Quiz started for session: " + sessionCode;
        } catch (Exception e) {
            return "Error starting quiz: " + e.getMessage();
        }
    }

    @MessageMapping("/quiz/{sessionCode}/join")
    @SendTo("/topic/quiz/{sessionCode}/participants")
    public String joinQuiz(@DestinationVariable String sessionCode) {
        return "New participant joined session: " + sessionCode;
    }

    @MessageMapping("/test")
    public void test(String message) {
        // Send test message to all subscribers
        messagingTemplate.convertAndSend("/topic/test", "Echo: " + message);
    }
    
    @MessageMapping("/ping")
    public void ping() {
        // Simple ping-pong for connection testing
        messagingTemplate.convertAndSend("/topic/pong", "pong");
    }
}