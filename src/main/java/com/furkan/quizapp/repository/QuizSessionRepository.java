package com.furkan.quizapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furkan.quizapp.entity.QuizSession;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    Optional<QuizSession> findBySessionCode(String sessionCode);
    boolean existsBySessionCode(String sessionCode);
    
}
