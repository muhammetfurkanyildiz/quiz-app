package com.furkan.quizapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furkan.quizapp.dto.DtoQuestion;
import com.furkan.quizapp.service.IGptService;

@RestController
@RequestMapping("/test")
public class GptTestController {

    @Autowired
    private IGptService gptService;

    @GetMapping
    public ResponseEntity<?> test(@RequestParam(defaultValue = "science") String category) {
        try {
            List<DtoQuestion> questions = gptService.generateQuestions(category);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}