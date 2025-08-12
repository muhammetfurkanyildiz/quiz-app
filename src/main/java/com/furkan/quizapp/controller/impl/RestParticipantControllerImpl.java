package com.furkan.quizapp.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.furkan.quizapp.controller.IRestParticipantController;
import com.furkan.quizapp.dto.AnswerQuestionRequest;
import com.furkan.quizapp.dto.AnswerReview;
import com.furkan.quizapp.dto.EmptyAnswerRequest;
import com.furkan.quizapp.dto.JoinSessionRequest;
import com.furkan.quizapp.dto.JoinSessionResponse;
import com.furkan.quizapp.dto.NextQuestionRequest;
import com.furkan.quizapp.dto.NextQuestionResponse;
import com.furkan.quizapp.dto.ParticipantInfo;
import com.furkan.quizapp.dto.ParticipantResult;
import com.furkan.quizapp.service.IParticipantService;
import com.furkan.quizapp.service.IQuizSessionService;

@RestController
@RequestMapping("/api/session")
public class RestParticipantControllerImpl implements IRestParticipantController {
    @Autowired
    private IParticipantService participantService;
    @Autowired
    private IQuizSessionService sessionService;
    

    @PostMapping("/join")
    @Override
    public ResponseEntity<JoinSessionResponse> joinSession(
            @RequestBody JoinSessionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        return ResponseEntity.ok(participantService.joinSession(request, username));
    }


    @GetMapping("/participants/{sessionCode}")
    @Override
    public ResponseEntity<List<ParticipantInfo>> getParticipants(@PathVariable String sessionCode) {
        return ResponseEntity.ok(participantService.getParticipantsBySessionCode(sessionCode));
    }


      @PostMapping("/answer")
      @Override
        public ResponseEntity<Void> submitAnswer(
                @RequestBody AnswerQuestionRequest request,
                @AuthenticationPrincipal UserDetails userDetails
        ) {
            String username = (userDetails != null) ? userDetails.getUsername() : request.nickname();

            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().build(); // Kullanıcı bilgisi eksik
            }

            participantService.submitAnswer(request, username);
            return ResponseEntity.ok().build();
        }


      @PostMapping("/next-question")
      @Override
        public ResponseEntity<NextQuestionResponse> getNextQuestion(
                @RequestBody NextQuestionRequest request,
                @AuthenticationPrincipal UserDetails userDetails) {

            String nickname = (userDetails != null)
                ? userDetails.getUsername()
                : request.nickname(); // login değilse request'ten al

            return ResponseEntity.ok(participantService.getNextQuestion(request.sessionCode(), nickname));
        }

        @PostMapping("/submit-empty")
      @Override
      public ResponseEntity<Void> submitEmptyAnswer(EmptyAnswerRequest request, UserDetails userDetails) {
        String resolvedNickname = (userDetails != null && userDetails.getUsername() != null)
            ? userDetails.getUsername()
            : request.nickname();

    participantService.submitEmpty(request.sessionCode(), resolvedNickname);
    return ResponseEntity.ok().build();
}


            @GetMapping("/results/{sessionCode}")
            @Override
    public ResponseEntity<List<ParticipantResult>> getResults(@PathVariable String sessionCode) {
        if (!sessionService.isSessionFinished(sessionCode)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null); // veya özel mesaj: "Quiz not finished yet"
        }
        return ResponseEntity.ok(participantService.getResults(sessionCode));
    }

    @GetMapping("/review")
    @Override
public ResponseEntity<List<AnswerReview>> getAnswers(
        @RequestParam String sessionCode,
        @RequestParam(required = false) String nickname,
        @AuthenticationPrincipal UserDetails userDetails
) {
    String resolvedNickname;

    if (userDetails != null) {
        // Giriş yapmış kullanıcı varsa username kullan
        resolvedNickname = userDetails.getUsername();
    } else if (nickname != null && !nickname.isBlank()) {
        // Giriş yapılmamışsa nickname zorunlu
        resolvedNickname = nickname;
    } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kullanıcı adı veya nickname zorunlu");
    }

    return ResponseEntity.ok(participantService.getAnswerReview(sessionCode, resolvedNickname));
}


    

        


}


    


    


    


