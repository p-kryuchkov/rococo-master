package io.student.rococo.test.web;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Artist;
import io.student.rococo.jupiter.annotation.ScreenshotTest;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.WebTest;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.page.ArtistPage;
import io.student.rococo.page.MainPage;
import io.student.rococo.page.component.ArtistCreateModal;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class ArtistWebTest {
    private static final String ARTIST_PHOTO = "images/artist.jpg";

    @Test
    @Artist
    @DisplayName("Should open all artists page for unauthorized user from header")
    void shouldOpenAllArtistsPageForUnauthorizedUserFromHeader() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openArtistsPageFromHeader()
                .checkPageLoaded()
                .checkArtistsExist()
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @Artist
    @DisplayName("Should open all artists page for unauthorized user from card")
    void shouldOpenAllArtistsPageForUnauthorizedUserFromCard() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openArtistsPageFromCard()
                .checkPageLoaded()
                .checkArtistsExist()
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @Artist
    @User
    @ApiLogin
    @ScreenshotTest(value = ARTIST_PHOTO, rewriteExpected = false)
    @DisplayName("Should find and edit artist for authorized user")
    void shouldFindAndEditArtistForAuthorizedUser(ArtistJson artist, BufferedImage expected) {
        final String updatedName = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();
        final String updatedBiography = RandomDataUtils.randomSentence(5);

        new ArtistPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsNotDisplayed()
                .searchArtist(artist.name())
                .checkArtistDisplayed(artist.name())
                .openArtistPage(artist.name())
                .checkPageLoaded()
                .checkArtistNameIsDisplayed(artist.name())
                .checkEditArtistButtonIsDisplayed()
                .openEditArtistForm()
                .editArtist(updatedName, updatedBiography, expected)
                .checkPageLoaded()
                .checkArtistNameIsDisplayed(updatedName)
                .checkArtistBiographyIsDisplayed(updatedBiography)
               // .assertPhotoScreenshotsMatch(expected)
                .assertDownloadedPhotoMatches(expected)
                .checkLoginButtonIsNotDisplayed();
    }

    @Test
    @User
    @ApiLogin
    @ScreenshotTest(value = ARTIST_PHOTO, rewriteExpected = false)
    @DisplayName("Should create and find artist for authorized user")
    void shouldCreateAndFindArtistForAuthorizedUser(BufferedImage expected) {
        final String artistName = RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname();
        final String biography = RandomDataUtils.randomSentence(5);

        ArtistPage artistPage = new ArtistPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsNotDisplayed()
                .checkAddArtistButtonDisplayed();

        ArtistCreateModal createArtistModal = artistPage.openCreateArtistModal()
                .checkModalLoaded();

        createArtistModal.createArtist(artistName, expected, biography)
                .checkModalClosed();

        artistPage.checkPageLoaded()
                .searchArtist(artistName)
                .checkArtistDisplayed(artistName)
                .openArtistPage(artistName)
                .checkPageLoaded()
                .checkArtistNameIsDisplayed(artistName)
                .checkArtistBiographyIsDisplayed(biography)
                //.assertPhotoScreenshotsMatch(expected)
                .assertDownloadedPhotoMatches(expected)
                .checkLoginButtonIsNotDisplayed();
    }

    @Test
    @Artist
    @DisplayName("Should not display edit artist button for unauthorized user")
    void shouldNotDisplayEditArtistButtonForUnauthorizedUser(ArtistJson artist) {
        new ArtistPage().open()
                .checkPageLoaded()
                .checkLoginButtonIsDisplayed()
                .searchArtist(artist.name())
                .checkArtistDisplayed(artist.name())
                .openArtistPage(artist.name())
                .checkPageLoaded()
                .checkArtistNameIsDisplayed(artist.name())
                .checkEditArtistButtonIsNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Test
    @Artist
    @DisplayName("Should not display add artist button for unauthorized user")
    void shouldNotDisplayAddArtistButtonForUnauthorizedUser() {
        new ArtistPage().open()
                .checkPageLoaded()
                .checkArtistsExist()
                .checkAddArtistButtonNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }
}