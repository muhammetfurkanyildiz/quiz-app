package com.furkan.quizapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname; // kullanıcı ismi (anonim olabilir)

    @ManyToOne
    private QuizSession quizSession;

    private int score;

    private LocalDateTime submittedAt;

    private int currentQuestionIndex = 0;
    private LocalDateTime currentQuestionStartTime;
    private boolean finished = false;
}
