package com.auth.app.security;

import com.auth.app.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Handles JWT generation and validation.
 *
 * Signing algorithm : HMAC-SHA256
 * Claims stored     : userId (Long), username (String), iat, exp
 * Secret source     : jwt.secret property (env variable in production)
 * Expiry            : jwt.expiration.ms property (default 86400000 = 24 hours)
 *
 * NEVER includes password or password hash in the token.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.ms}") long expirationMs) {

        this.secretKey   = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generate a signed JWT for the given user.
     *
     * @param user the authenticated user
     * @return signed JWT string
     */
    public String generateToken(User user) {
        Instant now    = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validate the token's signature and expiry.
     *
     * @param token JWT string
     * @return true if valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract the username (subject) from a token.
     * Call only after validateToken() returns true.
     *
     * @param token JWT string
     * @return username
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Calculate the token expiry as a LocalDateTime.
     * Used when persisting the JwtToken record to MySQL.
     *
     * @param token JWT string
     * @return expiry as LocalDateTime (UTC)
     */
    public LocalDateTime extractExpiry(String token) {
        Date expiry = parseClaims(token).getExpiration();
        return expiry.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
