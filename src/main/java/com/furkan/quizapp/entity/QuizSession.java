package com.furkan.quizapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.furkan.quizapp.enums.SessionState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionCode; // örn: 6 karakterli unique kod (örnek: X2F7LQ)

    @ManyToOne
    private Quiz quiz;

    private LocalDateTime createdAt;

    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private SessionState state;

    @OneToMany(mappedBy = "quizSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Participant> participants;
}
