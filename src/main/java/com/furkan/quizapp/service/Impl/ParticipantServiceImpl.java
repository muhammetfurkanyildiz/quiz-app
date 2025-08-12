package com.furkan.quizapp.service.Impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.furkan.quizapp.dto.AnswerQuestionRequest;
import com.furkan.quizapp.dto.AnswerReview;
import com.furkan.quizapp.dto.JoinSessionRequest;
import com.furkan.quizapp.dto.JoinSessionResponse;
import com.furkan.quizapp.dto.NextQuestionResponse;
import com.furkan.quizapp.dto.ParticipantInfo;
import com.furkan.quizapp.dto.ParticipantResult;
import com.furkan.quizapp.entity.Option;
import com.furkan.quizapp.entity.Participant;
import com.furkan.quizapp.entity.ParticipantAnswer;
import com.furkan.quizapp.entity.Question;
import com.furkan.quizapp.entity.QuizSession;
import com.furkan.quizapp.enums.SessionState;
import com.furkan.quizapp.repository.ParticipantAnswerRepository;
import com.furkan.quizapp.repository.ParticipantRepository;
import com.furkan.quizapp.repository.QuestionRepository;
import com.furkan.quizapp.repository.QuizSessionRepository;
import com.furkan.quizapp.service.IParticipantService;


@Service
public class ParticipantServiceImpl implements IParticipantService {

    @Autowired
    private QuizSessionRepository sessionRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private QuestionRepository questionRepository;

     @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ParticipantAnswerRepository participantAnswerRepository;

    @Override
    public JoinSessionResponse joinSession(JoinSessionRequest request, String usernameIfLoggedIn) {
        
        QuizSession session = sessionRepository.findBySessionCode(request.sessionCode())
                .orElseThrow(() -> new RuntimeException("Session not found"));
         if (session.getState() == SessionState.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session is already active.");
        }

        String nickname = (usernameIfLoggedIn != null && !usernameIfLoggedIn.isBlank()) 
                ? usernameIfLoggedIn 
                : request.nickname();

        if (nickname == null || nickname.isBlank()) {
            throw new RuntimeException("Nickname required for anonymous users.");
        }

        Participant participant = participantRepository
                .findByNicknameAndQuizSession(nickname, session)
                .orElse(null);

        if (participant == null) {
            participant = new Participant();
            participant.setNickname(nickname);
            participant.setQuizSession(session);
            participant.setScore(0);
            participant.setSubmittedAt(null);
            participant = participantRepository.save(participant);
        }

        return new JoinSessionResponse(
            participant.getId(),
            participant.getNickname(),
            session.getId()
        );
    }

    @Override
    public List<ParticipantInfo> getParticipantsBySessionCode(String sessionCode) {
    List<Participant> participants = participantRepository.findByQuizSession_SessionCode(sessionCode);
    return participants.stream()
        .map(p -> new ParticipantInfo(p.getId(), p.getNickname(), p.getSubmittedAt(), p.getScore()))
        .toList();
    }
   

private void checkAndBroadcastResultsIfFinished(QuizSession session) {
    List<Participant> participants = session.getParticipants();

    boolean everyoneFinished = participants.stream().allMatch(Participant::isFinished);

    if (everyoneFinished) {
        List<ParticipantResult> results = participants.stream()
                .sorted(Comparator
                    .comparingInt(Participant::getScore).reversed()
                    .thenComparing(Participant::getSubmittedAt))
                .map(p -> new ParticipantResult(
                    p.getNickname(),
                    p.getScore(),
                    p.getSubmittedAt()
                ))
                .toList();

        // Tüm kullanıcıların dinlediği bir topic’e gönder
        messagingTemplate.convertAndSend("/topic/quiz/" + session.getSessionCode() + "/results", results);
    }
}


    @Override
public void submitAnswer(AnswerQuestionRequest request, String nickname) {
    // 1. Session’ı bul
    QuizSession session = sessionRepository.findById(request.sessionId())
            .orElseThrow(() -> new RuntimeException("Session not found"));

    // 2. Participant’ı bul
    Participant participant = participantRepository.findByNicknameAndQuizSession(nickname, session)
            .orElseThrow(() -> new RuntimeException("Participant not found in session"));

    // 3. Question’ı bul
    Question question = questionRepository.findById(request.questionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));

    boolean isCorrect = false; // default olarak yanlış
    LocalDateTime startTime = participant.getCurrentQuestionStartTime();

    // 4. Süre kontrolü
    if (startTime != null) {
        long secondsElapsed = java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
        if (secondsElapsed <= 30) {
            // 5. Cevap kontrolü
            isCorrect = question.getOptions().stream()
                    .anyMatch(opt -> opt.isCorrect() && opt.getText().equalsIgnoreCase(request.selectedText()));
            if (isCorrect) {
                participant.setScore(participant.getScore() + 1);
            }
        } else {
            System.out.println("Süre aşıldı, cevap sayılmadı.");
        }
    }

    // 6. ParticipantAnswer kaydı
    ParticipantAnswer pa = ParticipantAnswer.builder()
            .participant(participant)
            .question(question)
            .selectedText(request.selectedText())
            .correct(isCorrect)
            .build();

    participantAnswerRepository.save(pa);

    // 7. Katılımcının ilerleyişini güncelle
    participant.setSubmittedAt(LocalDateTime.now());
    participant.setCurrentQuestionIndex(participant.getCurrentQuestionIndex() + 1);

    if (participant.getCurrentQuestionIndex() >= session.getQuiz().getQuestions().size()) {
        participant.setFinished(true);
    } else {
        participant.setCurrentQuestionStartTime(LocalDateTime.now());
    }

    participantRepository.save(participant);

    // 8. Eğer herkes bitirdiyse sonuçları yayınla
    checkAndBroadcastResultsIfFinished(session);
}


    @Override
public NextQuestionResponse getNextQuestion(String sessionCode, String nickname) {
    
    // 1. Session'ı bul
    QuizSession session = sessionRepository.findBySessionCode(sessionCode)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    // 2. Participant'ı bul
    Participant participant = participantRepository.findByNicknameAndQuizSession(nickname, session)
            .orElseThrow(() -> new RuntimeException("Participant not found"));

    // 3. Eğer quiz zaten bitmişse
    if (participant.isFinished()) {
        throw new RuntimeException("Quiz already finished for this participant.");
    }
    //participant.setCurrentQuestionIndex(participant.getCurrentQuestionIndex() + 1);
    List<Question> questions = session.getQuiz().getQuestions();
    
    int currentIndex = participant.getCurrentQuestionIndex();

    // 4. Tüm sorular bitmişse
    if (currentIndex >= questions.size()) {
        participant.setFinished(true);
        participantRepository.save(participant);
        throw new RuntimeException("No more questions left.");
    }

    // 5. Şu anki soruyu al
    Question question = questions.get(currentIndex);

    List<String> optionTexts = question.getOptions().stream()
            .map(option -> option.getText())
            .toList();

    // 6. Süreyi güncelle
    participant.setCurrentQuestionStartTime(LocalDateTime.now());
    participantRepository.save(participant);

    return new NextQuestionResponse(
            question.getId(),
            question.getQuestionText(),
            optionTexts,
            currentIndex + 1, // Kullanıcıya 1-based index gösteriyoruz
            questions.size(),
            30 // saniye sınırı
    );
}
    @Override
public void submitEmpty(String sessionCode, String nickname) {
    QuizSession session = sessionRepository.findBySessionCode(sessionCode)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    Participant participant = participantRepository.findByNicknameAndQuizSession(nickname, session)
            .orElseThrow(() -> new RuntimeException("Participant not found"));

    if (participant.isFinished()) return;

    List<Question> questions = session.getQuiz().getQuestions();
    int currentIndex = participant.getCurrentQuestionIndex();

    if (currentIndex >= questions.size()) {
        participant.setFinished(true);
        participantRepository.save(participant);
        return;
    }

    // Süre kontrolü: dolmadıysa boş geçme
    if (participant.getCurrentQuestionStartTime() != null) {
        long secondsElapsed = Duration.between(participant.getCurrentQuestionStartTime(), LocalDateTime.now()).getSeconds();
        if (secondsElapsed < 30) {
            // Süre henüz dolmadı, boş geçilmez
            return;
        }
    }

    Question currentQuestion = questions.get(currentIndex);

    // Boş cevap kaydı
    ParticipantAnswer answer = ParticipantAnswer.builder()
            .participant(participant)
            .question(currentQuestion)
            .selectedText("")  // boş cevap
            .correct(false)
            .build();

    participantAnswerRepository.save(answer);

    // Katılımcıyı ilerlet
    participant.setCurrentQuestionIndex(currentIndex + 1);
    participant.setSubmittedAt(LocalDateTime.now());

    if (participant.getCurrentQuestionIndex() >= questions.size()) {
        participant.setFinished(true);
    } else {
        participant.setCurrentQuestionStartTime(LocalDateTime.now());
    }

    participantRepository.save(participant);

    checkAndBroadcastResultsIfFinished(session);
}



    @Override
public List<ParticipantResult> getResults(String sessionCode) {
    QuizSession session = sessionRepository.findBySessionCode(sessionCode)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    List<Participant> participants = session.getParticipants();

    return participants.stream()
            .sorted(Comparator
                .comparingInt(Participant::getScore).reversed()
                .thenComparing(Participant::getSubmittedAt)) // eşit puan varsa önce bitiren öne geçer
            .map(p -> new ParticipantResult(
                p.getNickname(),
                p.getScore(),
                p.getSubmittedAt()
            ))
            .toList();
}

@Override
public List<AnswerReview> getAnswerReview(String sessionCode, String nickname) {
    QuizSession session = sessionRepository.findBySessionCode(sessionCode)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    Participant participant = participantRepository.findByNicknameAndQuizSession(nickname, session)
            .orElseThrow(() -> new RuntimeException("Participant not found"));

    List<ParticipantAnswer> answers = participantAnswerRepository.findByParticipant(participant);

    return answers.stream()
            .map(answer -> {
                Question question = answer.getQuestion();
                String correctText = question.getOptions().stream()
                        .filter(Option::isCorrect)
                        .findFirst()
                        .map(Option::getText)
                        .orElse("N/A");

                return new AnswerReview(
                        question.getId(),
                        session.getSessionCode(),
                        participant.getNickname(),
                        question.getQuestionText(),
                        answer.getSelectedText(),
                        correctText
                );
            })
            .toList();
}

 
}
