package io.student.rococo.test.web;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Museum;
import io.student.rococo.jupiter.annotation.ScreenshotTest;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.WebTest;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.page.MainPage;
import io.student.rococo.page.MuseumPage;
import io.student.rococo.page.component.MuseumCreateModal;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class MuseumWebTest {
    private static final String MUSEUM_PHOTO = "images/museum.jpg";

    @Test
    @Museum
    @DisplayName("Should open all museums page for unauthorized user from header")
    void shouldOpenAllMuseumsPageForUnauthorizedUserFromHeader() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openMuseumsPageFromHeader()
                .checkPageLoaded()
                .checkMuseumsExist()
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @Museum
    @DisplayName("Should open all museums page for unauthorized user from card")
    void shouldOpenAllMuseumsPageForUnauthorizedUserFromCard() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openMuseumsPageFromCard()
                .checkPageLoaded()
                .checkMuseumsExist()
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @Museum
    @DisplayName("Should find museum by search for unauthorized user")
    void shouldFindMuseumBySearchForUnauthorizedUser(MuseumJson museum) {
        new MuseumPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsDisplayed()
                .searchMuseum(museum.title())
                .checkMuseumDisplayed(museum.title());
    }

    @Test
    @Museum
    @DisplayName("Should open museum card for unauthorized user")
    void shouldOpenMuseumCardForUnauthorizedUser(MuseumJson museum) {
        new MuseumPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsDisplayed()
                .searchMuseum(museum.title())
                .checkMuseumDisplayed(museum.title())
                .openMuseumPage(museum.title())
                .checkPageLoaded()
                .checkMuseumNameIsDisplayed(museum.title())
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should open and close create museum modal for authorized user")
    void shouldOpenAndCloseCreateMuseumModalForAuthorizedUser() {
        MuseumCreateModal createMuseumModal = new MuseumPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsNotDisplayed()
                .checkAddMuseumButtonDisplayed()
                .openCreateMuseumModal()
                .checkModalLoaded();

        createMuseumModal.close()
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

        MuseumPage museumPage = new MuseumPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsNotDisplayed()
                .checkAddMuseumButtonDisplayed();

        MuseumCreateModal createMuseumModal = museumPage.openCreateMuseumModal()
                .checkModalLoaded();

        String defaultCountry = "Австралия";
        String defaultCity = "Сидней";
        createMuseumModal.createMuseum(museumName, defaultCountry, defaultCity, expected, description)
                .checkModalClosed();

        museumPage.checkPageLoaded()
                .searchMuseum(museumName)
                .checkMuseumDisplayed(museumName)
                .openMuseumPage(museumName)
                .checkPageLoaded()
                .checkMuseumNameIsDisplayed(museumName)
                .checkMuseumDescriptionIsDisplayed(description)
                //.assertPhotoScreenshotsMatch(expected)
                .assertDownloadedPhotoMatches(expected)
                .checkLoginButtonIsNotDisplayed();
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

        String updatedCountry = "Австрия";
        String updatedCity = "Вена";
        new MuseumPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsNotDisplayed()
                .searchMuseum(museum.title())
                .checkMuseumDisplayed(museum.title())
                .openMuseumPage(museum.title())
                .checkPageLoaded()
                .checkMuseumNameIsDisplayed(museum.title())
                .checkEditMuseumButtonIsDisplayed()
                .openEditMuseumForm()
                .editMuseum(updatedName, updatedCountry, updatedCity, updatedDescription, expected)
                .checkPageLoaded()
                .checkMuseumNameIsDisplayed(updatedName)
                .checkMuseumDescriptionIsDisplayed(updatedDescription)
                //.assertPhotoScreenshotsMatch(expected)
                .assertDownloadedPhotoMatches(expected)
                .checkLoginButtonIsNotDisplayed();
    }

    @Test
    @Museum
    @DisplayName("Should not display edit museum button for unauthorized user")
    void shouldNotDisplayEditMuseumButtonForUnauthorizedUser(MuseumJson museum) {
        new MuseumPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsDisplayed()
                .searchMuseum(museum.title())
                .checkMuseumDisplayed(museum.title())
                .openMuseumPage(museum.title())
                .checkPageLoaded()
                .checkMuseumNameIsDisplayed(museum.title())
                .checkEditMuseumButtonIsNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @Museum
    @DisplayName("Should not display add museum button for unauthorized user")
    void shouldNotDisplayAddMuseumButtonForUnauthorizedUser() {
        new MuseumPage().open()
                .checkPageLoaded()
                .checkMuseumsExist()
                .checkAddMuseumButtonNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }
}