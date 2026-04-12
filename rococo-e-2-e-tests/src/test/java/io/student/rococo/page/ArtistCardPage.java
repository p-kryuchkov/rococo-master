package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.ArtistUpdateModal;
import io.student.rococo.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ArtistCardPage extends BasePage<ArtistCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement artistName = card.$(".card-header");
    private final SelenideElement artistAvatar = card.$("[data-testid='avatar']");
    private final SelenideElement editArtistButton = card.$("[data-testid='edit-artist']");
    private final SelenideElement artistBiography = card.$("section p");

    private final ArtistUpdateModal artistUpdateModal = new ArtistUpdateModal();

    @Step("Open artist card page by id: {artistId}")
    public ArtistCardPage open(String artistId) {
        open(CFG.frontUrl() + "artist/" + artistId);
        return this;
    }

    @Override
    @Step("Check artist card page is loaded")
    public ArtistCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        artistName.shouldBe(visible);
        artistAvatar.shouldBe(visible);
        return this;
    }

    @Step("Check artist name '{artistName}' is displayed")
    public ArtistCardPage checkArtistNameIsDisplayed(String artistName) {
        this.artistName.shouldBe(visible).shouldHave(text(artistName));
        return this;
    }

    @Step("Check artist biography is displayed")
    public ArtistCardPage checkArtistBiographyIsDisplayed(String biographyText) {
        artistBiography.shouldBe(visible).shouldHave(text(biographyText));
        return this;
    }

    @Step("Check edit artist button is displayed")
    public ArtistCardPage checkEditArtistButtonIsDisplayed() {
        editArtistButton.shouldBe(visible);
        return this;
    }

    @Step("Check edit artist button is not displayed")
    public ArtistCardPage checkEditArtistButtonIsNotDisplayed() {
        editArtistButton.shouldNot(exist);
        return this;
    }

    @Step("Open edit artist form")
    public ArtistCardPage openEditArtistForm() {
        editArtistButton.shouldBe(visible).click();
        artistUpdateModal.checkModalOpened();
        return this;
    }

    @Step("Edit artist with name '{name}'")
    public ArtistCardPage editArtist(String name, String biography, BufferedImage photo) {
        artistUpdateModal.updateArtist(name, biography, photo);
        return this;
    }

    @Step("Screenshot photo")
    public File screenshotArtistPhoto() {
        return artistAvatar.screenshot();
    }

    @Step("Photo screenshots match")
    public ArtistCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        try {
            assertFalse(
                    new ScreenDiffResult(expected, ImageIO.read(screenshotArtistPhoto())),
                    "Screen comparison failure"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Step("Download artist photo")
    public BufferedImage downloadArtistPhoto() {
        try {
            String src = artistAvatar.$("img")
                    .shouldBe(visible)
                    .getAttribute("src");
            if (src == null || src.isBlank()) {
                throw new IllegalStateException("Artist photo src is empty");
            }
            return readImageBySrc(src);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download artist photo", e);
        }
    }

    @Step("Downloaded artist photo matches expected image")
    public ArtistCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertFalse(
                new ScreenDiffResult(expected, downloadArtistPhoto()),
                "Screen comparison failure"
        );
        return this;
    }
}