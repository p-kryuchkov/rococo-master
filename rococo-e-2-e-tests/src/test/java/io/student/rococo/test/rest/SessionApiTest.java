package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.SessionJson;
import io.student.rococo.model.UserJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class SessionApiTest extends BaseGatewayApiTest {
    @Test
    @User
    @ApiLogin
    @DisplayName("Should Return Session With Authorization")
    void shouldReturnSessionWithAuthorization(UserJson userJson, @Token String token) {
        final SessionJson session = gatewayApiClient.getSession(bearer(token));

        assertNotNull(session);
        assertEquals(userJson.username(), session.username());
        assertNotNull(session.issuedAt());
        assertNotNull(session.expiresAt());
        assertTrue(session.expiresAt().after(session.issuedAt()));
    }

    @Test
    @DisplayName("Should Return Empty Session Without Authorization")
    void shouldReturnEmptySessionWithoutAuthorization() {
        final SessionJson session = gatewayApiClient.getSession(null);

        assertNotNull(session);
        assertNull(session.username());
        assertNull(session.issuedAt());
        assertNull(session.expiresAt());
    }
}
