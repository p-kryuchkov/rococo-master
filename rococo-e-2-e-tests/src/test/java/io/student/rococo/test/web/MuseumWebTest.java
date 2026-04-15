package io.student.rococo.test.web;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Museum;
import io.student.rococo.jupiter.annotation.ScreenshotTest;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.WebTest;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.page.MainPage;
import io.student.rococo.page.MuseumPage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class MuseumWebTest {
    private static final String MUSEUM_PHOTO = "images/museum.jpg";
    private static final String DEFAULT_COUNTRY = "Австралия";
    private static final String DEFAULT_CITY = "Сидней";
    private static final String UPDATED_COUNTRY = "Австрия";
    private static final String UPDATED_CITY = "Вена";

    @Test
    @Museum
    @DisplayName("Should open all museums page for unauthorized user from header")
    void shouldOpenAllMuseumsPageForUnauthorizedUserFromHeader() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openMuseumsPageFromHeader()
                .checkOpenedForUnauthorizedUser();
    }

    @Test
    @Museum
    @DisplayName("Should open all museums page for unauthorized user from card")
    void shouldOpenAllMuseumsPageForUnauthorizedUserFromCard() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openMuseumsPageFromCard()
                .checkOpenedForUnauthorizedUser();
    }

    @Test
    @Museum
    @DisplayName("Should find museum by search for unauthorized user")
    void shouldFindMuseumBySearchForUnauthorizedUser(MuseumJson museum) {
        new MuseumPage().open()
                .checkOpenedForUnauthorizedUser()
                .searchAndCheckMuseumDisplayed(museum.title());
    }

    @Test
    @Museum
    @DisplayName("Should open museum card for unauthorized user")
    void shouldOpenMuseumCardForUnauthorizedUser(MuseumJson museum) {
        new MuseumPage().open()
                .checkOpenedForUnauthorizedUser()
                .openMuseumCard(museum.title())
                .checkOpenedForUnauthorizedUser(museum.title());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should open and close create museum modal for authorized user")
    void shouldOpenAndCloseCreateMuseumModalForAuthorizedUser() {
        new MuseumPage().open()
                .checkOpenedForAuthorizedUser()
                .openCreateMuseumModal()
                .checkModalLoaded()
                .close()
                .checkModalClosed();
    }

    @Test
    @User
    @ApiLogin
    @ScreenshotTest(value = MUSEUM_PHOTO, rewriteExpected = false)
    @DisplayName("Should create and find museum for authorized user")
    void shouldCreateAndFindMuseumForAuthorizedUser(BufferedImage expected) {
        final String museumName = RandomDataUtils.randomSentence(1);
        final String description = RandomDataUtils.randomSentence(5);

        MuseumPage page = new MuseumPage().open()
                .checkOpenedForAuthorizedUser();

        page.openCreateMuseumModal()
                .checkModalLoaded()
                .createMuseum(museumName, DEFAULT_COUNTRY, DEFAULT_CITY, expected, description)
                .checkModalClosed();

        page.openMuseumCard(museumName)
                .checkOpenedForAuthorizedUser(museumName)
                .checkMuseumLocationIsDisplayed(DEFAULT_COUNTRY, DEFAULT_CITY)
                .checkMuseumDescriptionIsDisplayed(description)
                .assertDownloadedPhotoMatches(expected);
    }

    @Test
    @Museum
    @User
    @ApiLogin
    @ScreenshotTest(value = MUSEUM_PHOTO, rewriteExpected = false)
    @DisplayName("Should find and edit museum for authorized user")
    void shouldFindAndEditMuseumForAuthorizedUser(MuseumJson museum, BufferedImage expected) {
        final String updatedName = RandomDataUtils.randomSentence(1);
        final String updatedDescription = RandomDataUtils.randomSentence(6);

        new MuseumPage().open()
                .checkOpenedForAuthorizedUser()
                .openMuseumCard(museum.title())
                .checkOpenedForAuthorizedUser(museum.title())
                .editMuseum(updatedName, UPDATED_COUNTRY, UPDATED_CITY, updatedDescription, expected)
                .checkOpenedForAuthorizedUser(updatedName)
                .checkMuseumLocationIsDisplayed(UPDATED_COUNTRY, UPDATED_CITY)
                .checkMuseumDescriptionIsDisplayed(updatedDescription)
                .assertDownloadedPhotoMatches(expected);
    }

    @Test
    @Museum
    @DisplayName("Should not display edit museum button for unauthorized user")
    void shouldNotDisplayEditMuseumButtonForUnauthorizedUser(MuseumJson museum) {
        new MuseumPage().open()
                .checkOpenedForUnauthorizedUser()
                .openMuseumCard(museum.title())
                .checkOpenedForUnauthorizedUser(museum.title());
    }

    @Test
    @Museum
    @DisplayName("Should not display add museum button for unauthorized user")
    void shouldNotDisplayAddMuseumButtonForUnauthorizedUser() {
        new MuseumPage().open()
                .checkOpenedForUnauthorizedUser();
    }
}
