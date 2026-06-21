package com.godotvillage.meowkanban.common.security;

import com.godotvillage.meowkanban.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final Duration expiration;

    public JwtTokenProvider(
            @Value("${meow-kanban.security.jwt.secret}") String secret,
            @Value("${meow-kanban.security.jwt.expiration-days:7}") long expirationDays) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofDays(expirationDays);
    }

    public String generateToken(User user, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expiration);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .claim("uid", String.valueOf(user.getId()))
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        String username = getUsername(token);
        return username != null && username.equals(userDetails.getUsername());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
