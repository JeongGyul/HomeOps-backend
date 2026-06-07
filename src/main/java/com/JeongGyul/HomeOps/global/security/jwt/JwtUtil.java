package com.JeongGyul.HomeOps.global.security.jwt;

import com.JeongGyul.HomeOps.global.security.principal.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    public JwtUtil(
            @Value("${jwt.token.secretKey}") String secret,
            @Value("${jwt.token.expiration.access}") Long accessExpiration,
            @Value("${jwt.token.expiration.refresh}") Long refreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
    }

    public String createAccessToken(CustomUserDetails user) {
        return createToken(user, accessExpiration, "access");
    }

    public String createRefreshToken(CustomUserDetails user) {
        return createToken(user, refreshExpiration, "refresh");
    }

    public String getMemberId(String token) {
        return getClaims(token).getPayload().getSubject();
    }
    public String getCategory(String token) {
        return getClaims(token).getPayload().get("category", String.class);
    }

    public long getExpiration(String token) {
        try {
            return getClaims(token).getPayload().getExpiration().getTime();
        } catch(ExpiredJwtException e) {
            return 0;
        }
    }

    public long getRefreshTokenExpiration() {
        return refreshExpiration.toMillis();
    }

    private String createToken(CustomUserDetails user, Duration expiration, String category) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(user.getUsername())
                .claim("category", category)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(secretKey);

        return builder.compact();
    }

    private Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);
    }
}
