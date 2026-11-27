package com.vibemitra.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-that-is-at-least-32-bytes-long!";

    @Test
    void issuedTokenRoundTripsUserId() {
        JwtService service = new JwtService(new JwtProperties(SECRET, 60), Clock.systemUTC());
        UUID userId = UUID.randomUUID();

        assertEquals(userId, service.parseUserId(service.issueToken(userId)).orElseThrow());
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService service = new JwtService(new JwtProperties(SECRET, 60), Clock.systemUTC());
        String token = service.issueToken(UUID.randomUUID());

        // Change the first character of the signature segment so the decoded signature really differs.
        String[] parts = token.split("\\.");
        char first = parts[2].charAt(0);
        String tamperedSignature = (first == 'A' ? 'B' : 'A') + parts[2].substring(1);
        String tampered = parts[0] + "." + parts[1] + "." + tamperedSignature;

        assertFalse(service.parseUserId(tampered).isPresent());
        assertFalse(service.parseUserId("not-a-jwt").isPresent());
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService issuer = new JwtService(new JwtProperties(SECRET, 60), Clock.systemUTC());
        JwtService verifier = new JwtService(
                new JwtProperties("another-secret-that-is-also-at-least-32-bytes-long", 60), Clock.systemUTC());

        assertFalse(verifier.parseUserId(issuer.issueToken(UUID.randomUUID())).isPresent());
    }

    @Test
    void expiredTokenIsRejected() {
        Instant issuedAt = Instant.parse("2026-01-01T00:00:00Z");
        JwtService issuer = new JwtService(new JwtProperties(SECRET, 5), Clock.fixed(issuedAt, ZoneOffset.UTC));
        String token = issuer.issueToken(UUID.randomUUID());

        JwtService laterVerifier = new JwtService(
                new JwtProperties(SECRET, 5),
                Clock.fixed(issuedAt.plus(Duration.ofMinutes(6)), ZoneOffset.UTC));
        assertTrue(laterVerifier.parseUserId(token).isEmpty());
    }

    @Test
    void shortSecretFailsFast() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JwtProperties("too-short", 60));
    }
}
