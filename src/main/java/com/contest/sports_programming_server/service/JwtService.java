package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.config.JwtProperties;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private Jws<Claims> extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
    }

    private <T> T extractClaimBody(String token, Function<Claims, T> claimsResolver) {
        final var claims = extractClaims(token);
        return claimsResolver.apply(claims.getBody());
    }

    public String extractUsername(String token) {
        return extractClaimBody(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaimBody(token, Claims::getExpiration);
    }

    public String generateToken(String username) {

        return createToken(new HashMap<>(), username);
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getValidity()))
                .signWith(getSecretKey())
                .compact();
    }

    private Key getSecretKey() {
        return new SecretKeySpec(Base64.getDecoder()
                .decode(jwtProperties.getSecretKey()),SignatureAlgorithm.HS256.getJcaName());
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
