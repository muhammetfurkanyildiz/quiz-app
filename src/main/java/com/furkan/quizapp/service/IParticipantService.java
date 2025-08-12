package com.furkan.quizapp.service;

import java.util.List;

import com.furkan.quizapp.dto.AnswerQuestionRequest;
import com.furkan.quizapp.dto.AnswerReview;
import com.furkan.quizapp.dto.JoinSessionRequest;
import com.furkan.quizapp.dto.JoinSessionResponse;
import com.furkan.quizapp.dto.NextQuestionResponse;
import com.furkan.quizapp.dto.ParticipantInfo;
import com.furkan.quizapp.dto.ParticipantResult;

public interface IParticipantService {
    JoinSessionResponse joinSession(JoinSessionRequest request, String usernameIfLoggedIn);
    List<ParticipantInfo> getParticipantsBySessionCode(String sessionCode);
    void submitAnswer(AnswerQuestionRequest request, String nickname);
    NextQuestionResponse getNextQuestion(String sessionCode, String nickname);
    void submitEmpty(String sessionCode, String nickname);
   
 List<ParticipantResult> getResults(String sessionCode);
 List<AnswerReview> getAnswerReview(String sessionCode, String nickname);


}