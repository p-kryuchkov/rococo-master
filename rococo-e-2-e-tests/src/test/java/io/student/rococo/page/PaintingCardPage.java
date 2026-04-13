package io.student.rococo.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.PaintingUpdateModal;
import io.student.rococo.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PaintingCardPage extends BasePage<PaintingCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement paintingName = card.$(".card-header");
    private final SelenideElement paintingArtist = card.$(".card-header + div");
    private final SelenideElement paintingImage = card.$("img");
    private final SelenideElement editPaintingButton = card.$("[data-testid='edit-painting']");
    private final SelenideElement paintingDescription = card.$("div.grid > div").$("div.m-4");
    private final PaintingUpdateModal paintingUpdateModal = new PaintingUpdateModal();

    @Step("Open painting card page by id: {paintingId}")
    public PaintingCardPage openPage(String paintingId) {
        Selenide.open(CFG.frontUrl() + "painting/" + paintingId);
        return this;
    }

    @Override
    @Step("Check painting card page loaded")
    public PaintingCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        paintingName.shouldBe(visible);
        paintingArtist.shouldBe(visible);
        return this;
    }

    @Step("Check painting name is displayed: {paintingTitle}")
    public PaintingCardPage checkPaintingNameIsDisplayed(String paintingTitle) {
        paintingName.shouldHave(text(paintingTitle));
        return this;
    }

    @Step("Check painting author is displayed: {artist}")
    public PaintingCardPage checkPaintingAuthorIsDisplayed(String artist) {
        paintingArtist.shouldHave(text(artist));
        return this;
    }

    @Step("Check painting description is displayed: {description}")
    public PaintingCardPage checkPaintingDescriptionIsDisplayed(String description) {
        paintingDescription.shouldHave(text(description));
        return this;
    }

    @Step("Check painting image is displayed")
    public PaintingCardPage checkPaintingImageIsDisplayed() {
        paintingImage.shouldBe(visible);
        return this;
    }

    @Step("Check painting image is loaded")
    public PaintingCardPage checkPaintingImageIsLoaded() {
        paintingImage.shouldBe(visible).shouldHave(image);
        return this;
    }

    @Step("Check edit painting button is displayed")
    public PaintingCardPage checkEditPaintingButtonIsDisplayed() {
        editPaintingButton.shouldBe(visible);
        return this;
    }

    @Step("Check edit painting button is not displayed")
    public PaintingCardPage checkEditPaintingButtonIsNotDisplayed() {
        editPaintingButton.shouldNot(exist);
        return this;
    }

    @Step("Open edit painting form")
    public PaintingUpdateModal openEditPaintingForm() {
        editPaintingButton.shouldBe(visible).click();
        return paintingUpdateModal;
    }

    @Step("Screenshot painting photo")
    public File screenshotPaintingPhoto() {
        return paintingImage.screenshot();
    }

    @Step("Painting photo screenshots match")
    public PaintingCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        try {
            assertFalse(
                    new ScreenDiffResult(expected, ImageIO.read(screenshotPaintingPhoto())),
                    "Screen comparison failure"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Step("Download painting photo")
    public BufferedImage downloadPaintingPhoto() {
        try {
            String src = paintingImage
                    .shouldBe(visible)
                    .getAttribute("src");

            if (src == null || src.isBlank()) {
                throw new IllegalStateException("Painting photo src is empty");
            }

            return readImageBySrc(src);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download painting photo", e);
        }
    }

    @Step("Downloaded painting photo matches expected image")
    public PaintingCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertFalse(
                new ScreenDiffResult(expected, downloadPaintingPhoto()),
                "Screen comparison failure"
        );
        return this;
    }

    @Step("Painting card is opened for unauthorized user")
    public PaintingCardPage checkOpenedForUnauthorizedUser() {
        return checkPageLoaded()
                .checkLoginButtonIsDisplayed()
                .checkEditPaintingButtonIsNotDisplayed();
    }

    @Step("Painting card is opened for authorized user")
    public PaintingCardPage checkOpenedForAuthorizedUser() {
        return checkPageLoaded()
                .checkLoginButtonIsNotDisplayed()
                .checkEditPaintingButtonIsDisplayed();
    }

    @Step("Check painting title: {title}")
    public PaintingCardPage checkPaintingTitle(String title) {
        return checkPaintingNameIsDisplayed(title);
    }

    @Step("Check painting details")
    public PaintingCardPage checkPaintingDetails(String title, String artist, String description) {
        return checkPaintingNameIsDisplayed(title)
                .checkPaintingAuthorIsDisplayed(artist)
                .checkPaintingDescriptionIsDisplayed(description);
    }

    @Step("Check painting image matches expected")
    public PaintingCardPage assertPaintingImageMatches(BufferedImage expected) {
        return assertDownloadedPhotoMatches(expected);
    }
}