package com.furkan.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furkan.quizapp.entity.Participant;
import com.furkan.quizapp.entity.ParticipantAnswer;

public interface ParticipantAnswerRepository extends JpaRepository<ParticipantAnswer, Long> {
    List<ParticipantAnswer> findByParticipant(Participant participant);
}