package com.furkan.quizapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.furkan.quizapp.controller.IRestAuthenticationController;
import com.furkan.quizapp.dto.AuthRequest;
import com.furkan.quizapp.dto.AuthResponse;
import com.furkan.quizapp.dto.DtoUser;
import com.furkan.quizapp.service.IAuthenticationService;

@RestController
public class RestAuthenticationControllerImpl implements IRestAuthenticationController {

    @Autowired
	private IAuthenticationService authenticationService;
    
    @PostMapping("/register")
    @Override
    public ResponseEntity<DtoUser> register(@RequestBody AuthRequest request) {
        
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
	@Override
	public ResponseEntity<AuthResponse> authenticate( @RequestBody AuthRequest input) {
		return ResponseEntity.ok(authenticationService.authenticate(input));
	}
}
