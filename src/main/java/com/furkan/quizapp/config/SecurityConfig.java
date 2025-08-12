package com.furkan.quizapp.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.furkan.quizapp.handler.AuthEntryPoint;
import com.furkan.quizapp.security.JWTAuthenticationFilter;
import com.furkan.quizapp.security.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public static final String REGISTER = "/register";
	public static final String AUTHENTICATE = "/authenticate";
	public static final String REFRESH_TOKEN = "/refreshToken";
	public static final String TEST = "/test/**";
	public static final String START = "/start/**";
	public static final String QUIZ = "/api/session/**";
	public static final String WS_ENDPOINT = "/ws/**";
	public static final String WS_APP = "/app/**";
	public static final String WS_TOPIC = "/topic/**";

	
	
	


	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Autowired
	private JWTAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
    private AuthEntryPoint authEntryPoint;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(withDefaults())
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(request -> request
				.requestMatchers(REGISTER, AUTHENTICATE, REFRESH_TOKEN, TEST, START, QUIZ, WS_ENDPOINT, WS_APP, WS_TOPIC).permitAll()
				.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
				.anyRequest().authenticated()
			)
            .oauth2Login(oauth -> oauth
                .successHandler(oAuth2AuthenticationSuccessHandler)
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(List.of("http://localhost:5173","http://127.0.0.1:5500"));

        configuration.setAllowedOrigins(List.of("http://localhost:5500", "http://localhost:5173","http://127.0.0.1:5500","http://localhost:5174"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Eğer cookie kullanıyorsan

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
