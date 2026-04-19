package com.ek.app.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${security.jwt.secret:dev-secret-key-change-this-to-a-long-random-value-1234567890}") String secret,
            @Value("${security.jwt.expiration-ms:86400000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        Date expiration = claims.getExpiration();
        return subject != null
                && subject.equals(expectedUsername)
                && expiration != null
                && expiration.after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
