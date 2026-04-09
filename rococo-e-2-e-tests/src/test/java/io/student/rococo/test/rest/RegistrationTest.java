package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.UserJson;
import io.student.rococo.service.UserClient;
import io.student.rococo.service.api.AuthApiClient;
import io.student.rococo.service.db.UserDbClient;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@RestTest
public class RegistrationTest {
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    @DisplayName("Should Register New User")
    void shouldRegisterNewUser() throws Exception {
        String username = RandomDataUtils.randomUsername();
        String password = "12345";

        Response<Void> response = authApiClient.register(username, password);

        assertTrue(response.isSuccessful());
    }

    @Test
    @User
    @DisplayName("Should Not Register User With Existing Username")
    void shouldNotRegisterUserWithExistingUsername(UserJson userJson) throws Exception {
        String password = "12345";

        Response<Void> response = authApiClient.register(userJson.username(), password);

        assertNotNull(response);
        assertFalse(response.isSuccessful());
    }

    @Test
    @User
    @DisplayName("Should Login Registered User")
    void shouldLoginRegisteredUser(UserJson userJson) throws Exception {
        String password = "12345";
        authApiClient.register(userJson.username(), password);
        String token = authApiClient.apiLogin(userJson.username(), password);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Should Not Login Unknown User")
    void shouldNotLoginUnknownUser() {
        assertThrows(RuntimeException.class,
                () -> authApiClient.apiLogin(RandomDataUtils.randomUsername(), "12345"));
    }
}
