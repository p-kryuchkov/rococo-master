package io.student.rococo.test.rest;

import io.student.rococo.service.api.AuthApiClient;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

public class RegistrationTest {
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void newUserShouldRegisteredByApiCall() throws IOException {
        final Response<Void> response = authApiClient.register(RandomDataUtils.randomUsername(), "12345");
        Assertions.assertEquals(201, response.code());
    }
}
