package com.furkan.quizapp.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.furkan.quizapp.enums.AuthProvider;
import com.furkan.quizapp.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles Google OAuth2 login success: ensures a local user exists and issues a JWT,
 * then redirects the client to the configured frontend with the token as a query param.
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${app.oauth2.redirectUri:http://localhost:5173/oauth2/success}")
    private String successRedirectUri;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = getAttribute(oAuth2User, "email");
        String name = getAttribute(oAuth2User, "name");
        if (email == null || email.isBlank()) {
            // Fallback to subject if email scope not granted
            email = getAttribute(oAuth2User, "sub");
        }

        ensureLocalUser(email, name);

        UserDetails principalForToken = new User(email, "", Collections.emptyList());
        String jwt = jwtService.generateToken(principalForToken);

        String redirectUrl = buildRedirectUrl(jwt);
        response.sendRedirect(redirectUrl);
    }

    private String buildRedirectUrl(String jwt) {
        String encoded = URLEncoder.encode(jwt, StandardCharsets.UTF_8);
        if (successRedirectUri.contains("?")) {
            return successRedirectUri + "&token=" + encoded;
        }
        return successRedirectUri + "?token=" + encoded;
    }

    private String getAttribute(OAuth2User user, String key) {
        Object value = user.getAttributes().get(key);
        return value == null ? null : String.valueOf(value);
    }

    private void ensureLocalUser(String email, String ignoredName) {
        if (email == null) {
            return;
        }
        userRepository.findByEmail(email).orElseGet(() -> {
            com.furkan.quizapp.entity.User newUser = com.furkan.quizapp.entity.User.builder()
                    .username(email)
                    .email(email)
                    .password("GOOGLE")
                    .authProvider(AuthProvider.GOOGLE)
                    .premium(false)
                    .remainingAttempts(3)
                    .lastResetTime(LocalDateTime.now())
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .build();
            return userRepository.save(newUser);
        });
    }
}


