package com.furkan.quizapp.service;

import com.furkan.quizapp.dto.AuthRequest;
import com.furkan.quizapp.dto.AuthResponse;
import com.furkan.quizapp.dto.DtoUser;

public interface IAuthenticationService {
    public DtoUser register(AuthRequest input);
    public AuthResponse authenticate(AuthRequest input);
}
