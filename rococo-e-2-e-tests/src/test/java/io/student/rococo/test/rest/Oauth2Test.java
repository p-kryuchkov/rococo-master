package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.UserJson;
import io.student.rococo.service.api.AuthApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTest
public class Oauth2Test {
    private final AuthApiClient authApiClient = new AuthApiClient();
    @Test
    @ApiLogin(username = "TestDefaultUser", password = "12345")
    public void oauth2Test(@Token String token, UserJson user) throws IOException {
        System.out.println(user);
        assertNotNull(token);
    }
}
