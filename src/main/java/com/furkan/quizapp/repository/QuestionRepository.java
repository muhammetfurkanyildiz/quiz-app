package com.furkan.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furkan.quizapp.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    
}