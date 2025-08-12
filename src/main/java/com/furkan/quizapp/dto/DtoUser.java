package com.furkan.quizapp.dto;

import java.time.LocalDateTime;

import com.furkan.quizapp.enums.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DtoUser {

    private Long id;

    private String username;

    private String email;

    private AuthProvider authProvider;

    private boolean premium;

    private int remainingAttempts;

    private LocalDateTime lastResetTime;

    private boolean enabled;

    private boolean accountNonLocked;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;
}
