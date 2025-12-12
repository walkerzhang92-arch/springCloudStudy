package com.walker.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtVerifier {

    private final byte[] keyBytes;

    public JwtVerifier(@Value("${jwt.secret}") String secret) {
        this.keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
