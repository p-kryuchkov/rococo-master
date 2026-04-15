package io.student.rococo.test.web;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.WebTest;
import io.student.rococo.model.UserJson;
import io.student.rococo.page.MainPage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class UserdataWebTest {

    private static final String PROFILE_PHOTO = "images/artist.jpg";

    @Test
    @User
    @ApiLogin
    @DisplayName("Should open profile modal for authorized user")
    void shouldOpenProfileModalForAuthorizedUser(UserJson user) {
        new MainPage().open()
                .checkPageLoaded()
                .openProfilePageFromHeader()
                .checkPageLoaded()
                .checkUsername(user.username())
                .checkFirstNamePlaceholder()
                .checkSurnamePlaceholder()
                .checkAvatarVisible();
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should update user firstname and surname")
    void shouldUpdateUserdata() {
        String firstName = RandomDataUtils.randomName();
        String surname = RandomDataUtils.randomName();

        new MainPage().open()
                .checkPageLoaded()
                .openProfilePageFromHeader()
                .checkPageLoaded()
                .updateProfile(firstName, surname)
                .checkPageLoaded()
                .openProfilePageFromHeader()
                .checkPageLoaded()
                .checkProfileData(firstName, surname);
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should update user photo")
    void shouldUpdateUserPhoto() {
        new MainPage().open()
                .checkPageLoaded()
                .openProfilePageFromHeader()
                .checkPageLoaded()
                .uploadPhoto(PROFILE_PHOTO)
                .submitUpdate()
                .checkPageLoaded()
                .openProfilePageFromHeader()
                .checkAvatarImageVisible();
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should logout from profile modal")
    void shouldLogoutFromProfileModal() {
        new MainPage().open()
                .checkPageLoaded()
                .openProfilePageFromHeader()
                .checkPageLoaded()
                .logout()
                .checkLoginButtonIsDisplayed();
    }
}