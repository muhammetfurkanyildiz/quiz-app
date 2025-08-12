package com.furkan.quizapp.controller;

import org.springframework.http.ResponseEntity;

import com.furkan.quizapp.dto.AuthRequest;
import com.furkan.quizapp.dto.AuthResponse;
import com.furkan.quizapp.dto.DtoUser;

public interface IRestAuthenticationController {

	public ResponseEntity<DtoUser> register(AuthRequest input);
	
	public ResponseEntity<AuthResponse> authenticate(AuthRequest input);
	
	//public ResponseEntity<AuthResponse> refreshToken(RefreshTokenRequest input);
}
