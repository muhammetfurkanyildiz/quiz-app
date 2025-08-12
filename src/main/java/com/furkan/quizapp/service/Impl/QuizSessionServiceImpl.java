package com.furkan.quizapp.service.Impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.furkan.quizapp.dto.CreateSessionResponse;
import com.furkan.quizapp.dto.ParticipantResult;
import com.furkan.quizapp.dto.QuizResponse;
import com.furkan.quizapp.dto.QuizStartPayload;
import com.furkan.quizapp.entity.Option;
import com.furkan.quizapp.entity.Participant;
import com.furkan.quizapp.entity.Question;
import com.furkan.quizapp.entity.Quiz;
import com.furkan.quizapp.entity.QuizSession;
import com.furkan.quizapp.enums.SessionState;
import com.furkan.quizapp.repository.ParticipantRepository;
import com.furkan.quizapp.repository.QuizRepository;
import com.furkan.quizapp.repository.QuizSessionRepository;
import com.furkan.quizapp.service.IQuizService;
import com.furkan.quizapp.service.IQuizSessionService;

@Service
public class QuizSessionServiceImpl implements IQuizSessionService {

    @Autowired
    private IQuizService quizService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ParticipantRepository participantRepository;

    private String generateSessionCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateUniqueSessionCode() {
        String code;
        do {
            code = generateSessionCode();
        } while (quizSessionRepository.existsBySessionCode(code));
        return code;
    }

    @Override
    public CreateSessionResponse createSession(Long quizId, String category) {
        Quiz quiz;

        if (quizId != null) {
            quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
        } else if (category != null && !category.isBlank()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            QuizResponse quizResponse = quizService.startQuiz(category, username);
            quiz = quizRepository.findById(quizResponse.quizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found for category: " + category));
        } else {
            throw new RuntimeException("Either quizId or category must be provided.");
        }

        QuizSession session = QuizSession.builder()
                .quiz(quiz)
                .createdAt(LocalDateTime.now())
                .sessionCode(generateUniqueSessionCode())
                .isActive(true)
                .state(SessionState.WAITING)
                .build();
        quizSessionRepository.save(session);

        return new CreateSessionResponse(
                session.getId(),
                session.getSessionCode(),
                quiz.getId(),
                quiz.getCategory()
        );
    }

    @Override
    public void startSession(String sessionCode) {
        QuizSession session = quizSessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Quiz quiz = session.getQuiz();
        List<Question> questions = quiz.getQuestions();

        if (questions.isEmpty()) {
            throw new RuntimeException("Quiz has no questions");
        }

        if (!session.isActive()) {
            throw new RuntimeException("Session is inactive.");
        }

        if (session.getState() != SessionState.WAITING) {
            throw new RuntimeException("Session is not in a waiting state.");
        }

        Question firstQuestion = questions.get(0);

        // Option'lardan sadece text'leri alıyoruz
        List<String> optionTexts = firstQuestion.getOptions().stream()
                .map(Option::getText)
                .toList();

        QuizStartPayload payload = new QuizStartPayload(
                session.getSessionCode(),
                firstQuestion.getId(),
                firstQuestion.getQuestionText(),
                optionTexts,
                1, // 1. soru
                questions.size(),
                30 // saniye sınırı
        );

        // WebSocket üzerinden frontend'e gönderiyoruz
        messagingTemplate.convertAndSend("/topic/quiz/" + sessionCode, payload);

        session.setState(SessionState.IN_PROGRESS);
        quizSessionRepository.save(session);
    }

    @Override
    public boolean isSessionFinished(String sessionCode) {
    QuizSession session = quizSessionRepository.findBySessionCode(sessionCode)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    List<Participant> participants = session.getParticipants();
    for (Participant participant : participants) {
        if (!participant.isFinished()) {
            return false; // Eğer herhangi bir katılımcı bitirmediyse, oturum bitmemiştir
        }
    }
    return true ;
    }

    @Override
public void forceEndSession(String sessionCode) {
    QuizSession session = quizSessionRepository.findBySessionCode(sessionCode)
        .orElseThrow(() -> new RuntimeException("Session not found"));

    List<Participant> participants = session.getParticipants();

    for (Participant p : participants) {
        if (!p.isFinished()) {
            p.setFinished(true);
            p.setSubmittedAt(LocalDateTime.now()); // Son bitiş zamanı olarak eklenebilir
        }
    }

    participantRepository.saveAll(participants);

    session.setState(SessionState.FINISHED);
    quizSessionRepository.save(session);

    // Skorları oluştur ve WebSocket ile gönder
    List<ParticipantResult> results = participants.stream()
            .sorted(Comparator
                    .comparingInt(Participant::getScore).reversed()
                    .thenComparing(Participant::getSubmittedAt))
            .map(p -> new ParticipantResult(
                    p.getNickname(),
                    p.getScore(),
                    p.getSubmittedAt()))
            .toList();

    messagingTemplate.convertAndSend("/topic/quiz/" + sessionCode + "/results", results);
}


}
