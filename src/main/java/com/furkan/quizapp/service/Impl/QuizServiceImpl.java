package com.furkan.quizapp.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.furkan.quizapp.dto.DtoAnswer;
import com.furkan.quizapp.dto.DtoOption;
import com.furkan.quizapp.dto.DtoQuestion;
import com.furkan.quizapp.dto.QuizAnswerRequest;
import com.furkan.quizapp.dto.QuizResponse;
import com.furkan.quizapp.dto.QuizResultResponse;
import com.furkan.quizapp.entity.Option;
import com.furkan.quizapp.entity.Question;
import com.furkan.quizapp.entity.Quiz;
import com.furkan.quizapp.entity.User;
import com.furkan.quizapp.exception.BaseException;
import com.furkan.quizapp.exception.ErrorMessage;
import com.furkan.quizapp.exception.MessageType;
import com.furkan.quizapp.repository.QuizRepository;
import com.furkan.quizapp.repository.UserRepository;
import com.furkan.quizapp.service.IGptService;
import com.furkan.quizapp.service.IQuizService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {
    

        @Autowired
        private  QuizRepository quizRepository;
        @Autowired
        private  UserRepository userRepository;
        @Autowired
        private  IGptService gptService;
        @Override
    public QuizResponse startQuiz(String category, String username) {
        
        
         // 2. Kullanıcıyı bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        

        if (!user.isPremium()) {
        LocalDateTime now = LocalDateTime.now();

        // Eğer 24 saat geçtiyse hakları sıfırla
        if (user.getLastResetTime() == null || user.getLastResetTime().plusHours(24).isBefore(now)) {
                user.setRemainingAttempts(3);
                user.setLastResetTime(now);
        }
          if (user.getRemainingAttempts() <= 0) {
         throw new BaseException(new ErrorMessage(MessageType.HAKKİNİZ_KALMADI, "Your remaining attempts are zero. Please wait until your attempts reset or upgrade to premium."));
        }
        user.setRemainingAttempts(user.getRemainingAttempts() - 1);
        userRepository.save(user);
        }

        List<DtoQuestion> questionDtos = gptService.generateQuestions(category);
        Quiz quiz = Quiz.builder()
                    .category(category)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();

        List<Question> questions = new ArrayList<>();

        // 4. Soruları ve seçenekleri ekle
        for (DtoQuestion questionDto : questionDtos) {

            Question question = Question.builder()
                    .questionText(questionDto.questionText())
                    .quiz(quiz)
                    .build();

            List<Option> options = questionDto.options().stream()
                    .map(opt -> Option.builder()
                            .text(opt.text())
                            .isCorrect(opt.isCorrect())
                            .question(question)
                            .build()
                    ).toList();

            question.setOptions(options);
            questions.add(question);
        }

        quiz.setQuestions(questions);

        // 5. DB'ye kaydet
        Quiz savedQuiz = quizRepository.save(quiz);

        // 6. Kullanıcıya döneceğin DTO'yu hazırla (cevaplar istemediğin için `isCorrect` yok sayılır)
        List<DtoQuestion> responseQuestions = savedQuiz.getQuestions().stream()
                .map(q -> new DtoQuestion(
                        q.getId(),
                        q.getQuestionText(),
                        q.getOptions().stream()
                                .map(o -> new DtoOption(o.getText(), false)) // isCorrect gizlenir
                                .toList()
                ))
                .toList();

        return new QuizResponse(savedQuiz.getId(), savedQuiz.getCategory(), responseQuestions);
    }
        @Override
public QuizResultResponse evaluateQuiz(QuizAnswerRequest request) {
    // 1. Quiz'i DB'den getir
    Quiz quiz = quizRepository.findById(request.quizId())
        .orElseThrow(() -> new RuntimeException("Quiz not found"));

    int correct = 0;

    for ( DtoAnswer answer : request.answers()) {
        // 2. Soruyu bul
        Question question = quiz.getQuestions().stream()
            .filter(q -> q.getId().equals(answer.questionId()))
            .findFirst()
            .orElse(null);

        if (question == null) continue;

        // 3. Doğru cevabı bul
        Option correctOption = question.getOptions().stream()
            .filter(Option::isCorrect)
            .findFirst()
            .orElse(null);

        if (correctOption == null) continue;

        // 4. Kullanıcının seçtiği şıkla karşılaştır
        if (correctOption.getText().equalsIgnoreCase(answer.selectedOptionText())) {
            correct++;
        }
    }

    int total = request.answers().size();
    int score = (int) Math.round((correct / (double) total) * 10);

    return new QuizResultResponse(total, correct, score);
}

}