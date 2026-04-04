package io.student.rococo.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUsernameFromSub() {
        final Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "splinter")
        );

        final JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String result = currentUserProvider.getUsername();

        assertEquals("splinter", result);
    }

    @Test
    void getUsernameFromNameWhenSubIsBlank() {
        final Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", " ")
        );

        final JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String result = currentUserProvider.getUsername();

        assertEquals(authentication.getName(), result);
    }

    @Test
    void returnNullWhenAuthenticationIsNotJwt() {
        final TestingAuthenticationToken authentication =
                new TestingAuthenticationToken("user", "password");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String result = currentUserProvider.getUsername();

        assertNull(result);
    }

    @Test
    void returnNullWhenAuthenticationIsNull() {
        SecurityContextHolder.getContext().setAuthentication(null);

        final String result = currentUserProvider.getUsername();

        assertNull(result);
    }
}