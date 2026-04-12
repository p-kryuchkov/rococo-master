package io.student.rococo.test.web;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.WebTest;
import io.student.rococo.model.UserJson;
import io.student.rococo.page.MainPage;
import io.student.rococo.page.RegisterPage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class AuthWebTest {
    @Test
    @DisplayName("Successful New User Registration")
    void shouldRegisterNewUser() {
        final String username = RandomDataUtils.randomUsername();
        final String password = RandomDataUtils.randomPassword();

        new RegisterPage().openPage()
                .checkPageLoaded()
                .register(username, password, password)
                .checkSuccessRegisterPageLoaded()
                .loginAfterRegistration()
                .openLoginPageFromHeader()
                .login(username, password)
                .checkPageLoaded();
    }

    @Test
    @User
    @DisplayName("Registration Error: Existing User")
    void shouldNotRegisterUserWithExistingUsername(UserJson userJson) {
        final String password = RandomDataUtils.randomPassword();

        new RegisterPage().openPage()
                .checkPageLoaded()
                .register(userJson.username(), password, password)
                .checkUsernameError("Username `" + userJson.username() + "` already exists");
    }

    @Test
    @DisplayName("Registration Error: Password And Confirm Password Are Not Equal")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        final String username = RandomDataUtils.randomUsername();
        final String password = RandomDataUtils.randomPassword();
        final String confirmPassword = RandomDataUtils.randomPassword();

        new RegisterPage().openPage()
                .checkPageLoaded()
                .register(username, password, confirmPassword)
                .checkPasswordError("Passwords should be equal");
    }

    @Test
    @User
    @DisplayName("Main Page Should Be Displayed After Success Login")
    void shouldDisplayMainPageAfterSuccessfulLogin(UserJson userJson) {
        final String password = "12345";

        new MainPage().open()
                .openLoginPageFromHeader()
                .login(userJson.username(), password)
                .checkPageLoaded()
                .checkLoginButtonIsNotDisplayed();
    }

    @Test
    @User
    @DisplayName("Login error: Bad Credentials")
    void shouldShowErrorIfBadCredentials(UserJson userJson) {
        final String password = RandomDataUtils.randomPassword();

        new MainPage().open()
                .openLoginPageFromHeader()
                .setUsername(userJson.username())
                .setPassword(password)
                .submitBadCredentials()
                .checkLoginError("Неверные учетные данные пользователя");
    }

    @Test
    @User
    @ApiLogin()
    @DisplayName("Success Logout")
    void shouldLogout(UserJson userJson) {
        new MainPage().open()
                .openProfilePageFromHeader()
                .logout()
                .checkLoginButtonIsDisplayed();
    }
}
