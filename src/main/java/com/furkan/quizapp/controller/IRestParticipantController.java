package com.furkan.quizapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.furkan.quizapp.dto.AnswerQuestionRequest;
import com.furkan.quizapp.dto.AnswerReview;
import com.furkan.quizapp.dto.EmptyAnswerRequest;
import com.furkan.quizapp.dto.JoinSessionRequest;
import com.furkan.quizapp.dto.JoinSessionResponse;
import com.furkan.quizapp.dto.NextQuestionRequest;
import com.furkan.quizapp.dto.NextQuestionResponse;
import com.furkan.quizapp.dto.ParticipantInfo;
import com.furkan.quizapp.dto.ParticipantResult;

public interface IRestParticipantController {
     ResponseEntity<JoinSessionResponse> joinSession(
        @RequestBody JoinSessionRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    );
    ResponseEntity<List<ParticipantInfo>> getParticipants(@PathVariable String sessionCode);
    ResponseEntity<Void> submitAnswer(@RequestBody AnswerQuestionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    );
     ResponseEntity<NextQuestionResponse> getNextQuestion(
        @RequestBody NextQuestionRequest request,
        @AuthenticationPrincipal UserDetails userDetails);
      ResponseEntity<Void> submitEmptyAnswer(
        @RequestBody EmptyAnswerRequest request,
        @AuthenticationPrincipal UserDetails userDetails);
        
        ResponseEntity<List<ParticipantResult>> getResults(@PathVariable String sessionCode);
         ResponseEntity<List<AnswerReview>> getAnswers(
        @RequestParam String sessionCode,
        @RequestParam(required = false) String nickname,
        @AuthenticationPrincipal UserDetails userDetails
); 


}
