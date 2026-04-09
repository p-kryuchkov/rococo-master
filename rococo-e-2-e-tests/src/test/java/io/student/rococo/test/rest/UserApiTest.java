package io.student.rococo.test.rest;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Token;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.RestTest;
import io.student.rococo.model.UserJson;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class UserApiTest extends BaseGatewayApiTest {

    @Test
    @ApiLogin
    @DisplayName("Should Return Current User With Authorization")
    void shouldReturnCurrentUserWithAuthorization(@Token String token) throws Exception {
        final UserJson result = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        assertEquals("TestDefaultUser", result.username());
        assertNotNull(result.firstname());
        assertNotNull(result.lastname());
    }

    @Test
    @ApiLogin
    @DisplayName("Should Update User Firstname With Authorization")
    void shouldUpdateUserFirstnameWithAuthorization(@Token String token) throws Exception {
        String updatedFirstname = RandomDataUtils.randomName();

        final UserJson currentUser = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        final UserJson updateRequest = new UserJson(
                null,
                currentUser.username(),
                updatedFirstname,
                null,
                null
        );

        final UserJson updatedUser = gatewayApiClient.updateUser(
                bearer(token),
                updateRequest
        );

        assertEquals(currentUser.username(), updatedUser.username());
        assertEquals(updatedFirstname, updatedUser.firstname());
        assertEquals(currentUser.lastname(), updatedUser.lastname());
        assertEquals(currentUser.avatar(), updatedUser.avatar());

        final UserJson result = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        assertEquals(currentUser.username(), result.username());
        assertEquals(updatedFirstname, result.firstname());
        assertEquals(currentUser.lastname(), result.lastname());
        assertEquals(currentUser.avatar(), result.avatar());
    }

    @Test
    @ApiLogin
    @DisplayName("Should Update User Lastname With Authorization")
    void shouldUpdateUserLastnameWithAuthorization(@Token String token) throws Exception {
        String updatedLastname = RandomDataUtils.randomName();

        final UserJson currentUser = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        final UserJson updateRequest = new UserJson(
                currentUser.id(),
                currentUser.username(),
                null,
                updatedLastname,
                null
        );

        final UserJson updatedUser = gatewayApiClient.updateUser(
                bearer(token),
                updateRequest
        );

        assertEquals(currentUser.username(), updatedUser.username());
        assertEquals(currentUser.firstname(), updatedUser.firstname());
        assertEquals(updatedLastname, updatedUser.lastname());
        assertEquals(currentUser.avatar(), updatedUser.avatar());

        final UserJson result = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        assertEquals(currentUser.username(), result.username());
        assertEquals(currentUser.firstname(), result.firstname());
        assertEquals(updatedLastname, result.lastname());
        assertEquals(currentUser.avatar(), result.avatar());
    }

    @Test
    @ApiLogin
    @DisplayName("Should Update User Avatar With Authorization")
    void shouldUpdateUserAvatarWithAuthorization(@Token String token) throws Exception {
        final UserJson currentUser = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        final UserJson updateRequest = new UserJson(
                null,
                currentUser.username(),
                null,
                null,
                IMAGE
        );

        final UserJson updatedUser = gatewayApiClient.updateUser(
                bearer(token),
                updateRequest
        );

        assertEquals(currentUser.username(), updatedUser.username());
        assertEquals(currentUser.firstname(), updatedUser.firstname());
        assertEquals(currentUser.lastname(), updatedUser.lastname());
        assertEquals(IMAGE, updatedUser.avatar());

        final UserJson result = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        assertEquals(currentUser.username(), result.username());
        assertEquals(currentUser.firstname(), result.firstname());
        assertEquals(currentUser.lastname(), result.lastname());
        assertEquals(IMAGE, result.avatar());
    }

    @Test
    @ApiLogin
    @DisplayName("Should Update User With Authorization")
    void shouldUpdateUserWithAuthorization(@Token String token) throws Exception {
        String updatedFirstname = RandomDataUtils.randomName();
        String updatedLastname = RandomDataUtils.randomSurname();

        final UserJson currentUser = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        final UserJson updateRequest = new UserJson(
                currentUser.id(),
                currentUser.username(),
                updatedFirstname,
                updatedLastname,
                IMAGE
        );

        final UserJson updatedUser = gatewayApiClient.updateUser(
                bearer(token),
                updateRequest
        );

        assertEquals(currentUser.username(), updatedUser.username());
        assertEquals(updatedFirstname, updatedUser.firstname());
        assertEquals(updatedLastname, updatedUser.lastname());
        assertEquals(IMAGE, updatedUser.avatar());

        final UserJson result = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        assertEquals(currentUser.username(), result.username());
        assertEquals(updatedFirstname, result.firstname());
        assertEquals(updatedLastname, result.lastname());
        assertEquals(IMAGE, result.avatar());
    }

    @Test
    @ApiLogin
    @DisplayName("Should Not Update User Without Authorization")
    void shouldNotUpdateUserWithoutAuthorization(@Token String token) throws Exception {
        String updatedFirstname = RandomDataUtils.randomName();

        final UserJson beforeUpdate = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        final UserJson updateRequest = new UserJson(
                null,
                beforeUpdate.username(),
                updatedFirstname,
                null,
                null
        );

        Response<UserJson> response = gatewayApiClient.updateUserRaw(
                null,
                updateRequest
        );

        assertEquals(401, response.code());

        final UserJson actualUser = gatewayApiClient.getCurrentUser(
                bearer(token)
        );

        assertEquals(beforeUpdate.username(), actualUser.username());
        assertEquals(beforeUpdate.firstname(), actualUser.firstname());
        assertEquals(beforeUpdate.lastname(), actualUser.lastname());
        assertEquals(beforeUpdate.avatar(), actualUser.avatar());
    }
}