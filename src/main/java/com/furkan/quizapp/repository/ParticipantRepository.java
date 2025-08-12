
package com.furkan.quizapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furkan.quizapp.entity.Participant;
import com.furkan.quizapp.entity.QuizSession;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByQuizSession_SessionCode(String sessionCode);
    Optional<Participant> findByNicknameAndQuizSession(String nickname, QuizSession session);
    

}
