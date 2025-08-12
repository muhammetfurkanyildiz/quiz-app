package com.furkan.quizapp.service;

import com.furkan.quizapp.dto.CreateSessionResponse;

public interface IQuizSessionService {
    CreateSessionResponse createSession(Long quizId,String category);
    void startSession(String sessionCode);
    boolean isSessionFinished(String sessionCode);
    public void forceEndSession(String sessionCode);

}
