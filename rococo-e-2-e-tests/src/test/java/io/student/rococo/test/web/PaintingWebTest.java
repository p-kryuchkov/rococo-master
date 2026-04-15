package io.student.rococo.test.web;

import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.jupiter.annotation.Artist;
import io.student.rococo.jupiter.annotation.Museum;
import io.student.rococo.jupiter.annotation.Painting;
import io.student.rococo.jupiter.annotation.ScreenshotTest;
import io.student.rococo.jupiter.annotation.User;
import io.student.rococo.jupiter.annotation.meta.WebTest;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.MuseumJson;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.page.ArtistCardPage;
import io.student.rococo.page.MainPage;
import io.student.rococo.page.PaintingPage;
import io.student.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class PaintingWebTest {
    private static final String PAINTING_PHOTO = "images/painting.jpg";
    private static final String DEFAULT_ARTIST_NAME = "Эдвард Мунк";
    private static final String DEFAULT_MUSEUM_TITLE = "Лувр";

    @Test
    @Painting
    @DisplayName("Should open all paintings page for unauthorized user from header")
    void shouldOpenAllPaintingsPageForUnauthorizedUserFromHeader() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openPaintingsPageFromHeader()
                .checkOpenedForUnauthorizedUser();
    }

    @Test
    @Painting
    @DisplayName("Should open all paintings page for unauthorized user from card")
    void shouldOpenAllPaintingsPageForUnauthorizedUserFromCard() {
        new MainPage().open()
                .checkNavigationCardsAreDisplayed()
                .openPaintingsPageFromCard()
                .checkOpenedForUnauthorizedUser();
    }

    @Test
    @Painting
    @DisplayName("Should find painting by search for unauthorized user")
    void shouldFindPaintingBySearchForUnauthorizedUser(PaintingJson painting) {
        new PaintingPage().open()
                .checkOpenedForUnauthorizedUser()
                .searchAndCheckPaintingDisplayed(painting.title());
    }

    @Test
    @Painting
    @DisplayName("Should open painting card for unauthorized user")
    void shouldOpenPaintingCardForUnauthorizedUser(PaintingJson painting) {
        new PaintingPage().open()
                .checkOpenedForUnauthorizedUser()
                .openPaintingCard(painting.title())
                .checkOpenedForUnauthorizedUser()
                .checkPaintingTitle(painting.title());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should open and close create painting modal for authorized user")
    void shouldOpenAndCloseCreatePaintingModalForAuthorizedUser() {
        new PaintingPage().open()
                .checkOpenedForAuthorizedUser()
                .openCreatePaintingModal()
                .checkModalLoadedWithArtistSelect()
                .close()
                .checkModalClosed();
    }

    @Test
    @Artist
    @User
    @ApiLogin
    @ScreenshotTest(value = PAINTING_PHOTO, rewriteExpected = false)
    @DisplayName("Should create and find painting for authorized user")
    void shouldCreateAndFindPaintingForAuthorizedUser( BufferedImage expected) {
        final String title = RandomDataUtils.randomSentence(1);
        final String description = RandomDataUtils.randomSentence(5);

        new PaintingPage().open()
                .checkOpenedForAuthorizedUser()
                .createPaintingAndOpenCard(title, DEFAULT_ARTIST_NAME, DEFAULT_MUSEUM_TITLE, expected, description)
                .checkOpenedForAuthorizedUser()
                .checkPaintingDetails(title, DEFAULT_ARTIST_NAME, description)
                .assertPaintingImageMatches(expected);
    }

    @Test
    @Painting
    @User
    @ApiLogin
    @ScreenshotTest(value = PAINTING_PHOTO, rewriteExpected = false)
    @DisplayName("Should find and edit painting for authorized user")
    void shouldFindAndEditPaintingForAuthorizedUser(PaintingJson painting,
                                                    BufferedImage expected) {
        final String updatedTitle = RandomDataUtils.randomSentence(1);
        final String updatedDescription = RandomDataUtils.randomSentence(6);

        new PaintingPage().open()
                .checkOpenedForAuthorizedUser()
                .openPaintingCard(painting.title())
                .checkOpenedForAuthorizedUser()
                .openEditPaintingForm()
                .editPainting(updatedTitle, DEFAULT_ARTIST_NAME, DEFAULT_MUSEUM_TITLE, updatedDescription, expected)
                .checkOpenedForAuthorizedUser()
                .checkPaintingDetails(updatedTitle, DEFAULT_ARTIST_NAME, updatedDescription)
                .assertPaintingImageMatches(expected);
    }

    @Test
    @Painting
    @DisplayName("Should not display edit painting button for unauthorized user")
    void shouldNotDisplayEditPaintingButtonForUnauthorizedUser(PaintingJson painting) {
        new PaintingPage().open()
                .checkOpenedForUnauthorizedUser()
                .openPaintingCard(painting.title())
                .checkOpenedForUnauthorizedUser()
                .checkEditPaintingButtonIsNotDisplayed();
    }

    @Test
    @Painting
    @DisplayName("Should not display add painting button for unauthorized user")
    void shouldNotDisplayAddPaintingButtonForUnauthorizedUser() {
        new PaintingPage().open()
                .checkOpenedForUnauthorizedUser();
    }

    @Test
    @Artist
    @User
    @ApiLogin
    @ScreenshotTest(value = PAINTING_PHOTO, rewriteExpected = false)
    @DisplayName("Should create and display painting on artist page for authorized user")
    void shouldCreateAndDisplayPaintingOnArtistPageForAuthorizedUser(ArtistJson artist,
                                                                     BufferedImage expected) {
        final String paintingTitle = RandomDataUtils.randomSentence(1);
        final String description = RandomDataUtils.randomSentence(5);

        new ArtistCardPage().open(artist.id().toString())
                .checkOpenedForAuthorizedUser(artist.name())
                .addPainting(paintingTitle, DEFAULT_MUSEUM_TITLE, expected, description)
                .checkOpenedForAuthorizedUser(artist.name())
                .checkPaintingCardDisplayed(paintingTitle);
    }

    @Test
    @Artist
    @Museum(title = "Лувр")
    @User
    @ApiLogin
    @ScreenshotTest(value = PAINTING_PHOTO, rewriteExpected = false)
    @DisplayName("Should display all created paintings on artist page for authorized user")
    void shouldDisplayAllCreatedPaintingsOnArtistPageForAuthorizedUser(ArtistJson artist,
                                                                       MuseumJson museum,
                                                                       BufferedImage expected) {
        final String firstPaintingTitle = RandomDataUtils.randomSentence(1);
        final String secondPaintingTitle = RandomDataUtils.randomSentence(1);

        new ArtistCardPage().open(artist.id().toString())
                .checkOpenedForAuthorizedUser(artist.name())
                .addPainting(firstPaintingTitle, museum.title(), expected, RandomDataUtils.randomSentence(5))
                .addPainting(secondPaintingTitle, museum.title(), expected, RandomDataUtils.randomSentence(5))
                .checkOpenedForAuthorizedUser(artist.name())
                .checkPaintingsCount(2)
                .checkPaintingsAreDisplayed(firstPaintingTitle, secondPaintingTitle);
    }
}
