package com.furkan.quizapp.service.Impl;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.furkan.quizapp.dto.AuthRequest;
import com.furkan.quizapp.dto.AuthResponse;
import com.furkan.quizapp.dto.DtoUser;
import com.furkan.quizapp.entity.User;
import com.furkan.quizapp.enums.AuthProvider;
import com.furkan.quizapp.exception.BaseException;
import com.furkan.quizapp.exception.ErrorMessage;
import com.furkan.quizapp.exception.MessageType;
import com.furkan.quizapp.repository.UserRepository;
import com.furkan.quizapp.security.CustomUserDetailsService;
import com.furkan.quizapp.security.JwtService;
import com.furkan.quizapp.service.IAuthenticationService;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
	private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public DtoUser register(AuthRequest input) {
        // En az username veya email girilmiş mi kontrolü yapabilirsin (opsiyonel)
        if ((input.getUsername() == null || input.getUsername().isBlank()) 
                && (input.getEmail() == null || input.getEmail().isBlank())) {
            throw new IllegalArgumentException("Username or Email must be provided");
        }

        User userToSave = createUser(input);
        User savedUser = userRepository.save(userToSave);

        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(savedUser, dtoUser);
        return dtoUser;
    }

    private User createUser(AuthRequest input) {
        return User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .authProvider(AuthProvider.LOCAL)
                .premium(false)
                .remainingAttempts(3)
                .lastResetTime(LocalDateTime.now())
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
    }

            @Override
    @SuppressWarnings("UseSpecificCatch")
        public AuthResponse authenticate(AuthRequest input) {
            try {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword());
                authenticationProvider.authenticate(authenticationToken);

                // UserDetails nesnesi yükleniyor
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(input.getUsername());

                String accessToken = jwtService.generateToken(userDetails);
                return new AuthResponse(accessToken);

            } catch (Exception e) {
                throw new BaseException(new ErrorMessage(MessageType.USERNAME_OR_PASSWORD_INVALID, e.getMessage()));
            }
        }

}
