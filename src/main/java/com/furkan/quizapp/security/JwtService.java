package com.furkan.quizapp.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.furkan.quizapp.exception.BaseException;
import com.furkan.quizapp.exception.ErrorMessage;
import com.furkan.quizapp.exception.MessageType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    @Value("${app.SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    public String generateToken(UserDetails userDetails) {
		Map<String, Object> claimsMap   =  new HashMap<>();
		claimsMap.put("role", "ADMIN");
		
		return Jwts.builder()
		.setSubject(userDetails.getUsername())
		.addClaims(claimsMap)
		.setIssuedAt(new Date())
		.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
		.signWith(getKey(), SignatureAlgorithm.HS256)
		.compact();
	}

    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public  Object getClaimsByKey(String token , String key) {
		Claims claims =  getClaims(token);
		return claims.get(key);
	}
	
	
	public  Claims getClaims(String token) {
		Claims claims = Jwts
				.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token).getBody();
		return claims;
	}
	
	public <T> T exportToken(String token , Function<Claims, T> claimsFunction) {
		Claims claims =  getClaims(token);
		return claimsFunction.apply(claims);
	}
	
	public String getUsernameByToken(String token) {
		return exportToken(token, Claims::getSubject);
	}
	
	
	public boolean isTokenExpired(String token) {
		Date expiredDate= exportToken(token, Claims::getExpiration);
		return new Date().before(expiredDate);
	}

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(new ErrorMessage(MessageType.TOKEN_INVALID, e.getMessage()));
        }
    }
	
	
	


   

}
