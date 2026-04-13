package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.PaintingUpdateModal;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingCardPage extends BasePage<PaintingCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement paintingName = card.$(".card-header");
    private final SelenideElement paintingArtist = card.$(".card-header + div");
    private final SelenideElement paintingImage = card.$("img");
    private final SelenideElement editPaintingButton = card.$("[data-testid='edit-painting']");
    private final SelenideElement paintingDescription = card.$("div.grid > div div.m-4");

    @Override
    protected PaintingCardPage self() {
        return this;
    }

    @Step("Open painting card page by id: {paintingId}")
    public PaintingCardPage openPage(String paintingId) {
        return super.openPage(CFG.frontUrl() + "painting/" + paintingId);
    }

    @Override
    @Step("Check painting card page loaded")
    public PaintingCardPage checkPageLoaded() {
        super.checkPageLoaded();
        checkVisible(card, paintingName, paintingArtist);
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

    @Step("Check painting name is displayed: {paintingTitle}")
    public PaintingCardPage checkPaintingNameIsDisplayed(String paintingTitle) {
        checkTitle(paintingName, paintingTitle);
        return this;
    }

    @Step("Check painting author is displayed: {artist}")
    public PaintingCardPage checkPaintingAuthorIsDisplayed(String artist) {
        paintingArtist.shouldBe(visible).shouldHave(text(artist));
        return this;
    }

    @Step("Check painting description is displayed: {description}")
    public PaintingCardPage checkPaintingDescriptionIsDisplayed(String description) {
        paintingDescription.shouldBe(visible).shouldHave(text(description));
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
        checkVisible(editPaintingButton);
        return this;
    }

    @Step("Check edit painting button is not displayed")
    public PaintingCardPage checkEditPaintingButtonIsNotDisplayed() {
        checkDoesNotExist(editPaintingButton);
        return this;
    }

    @Step("Open edit painting form")
    public PaintingUpdateModal openEditPaintingForm() {
        editPaintingButton.shouldBe(visible).click();
        return new PaintingUpdateModal().checkModalLoaded();
    }

    @Step("Painting photo screenshots match")
    public PaintingCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        assertScreenshotMatches(expected, paintingImage, "painting photo");
        return this;
    }

    @Step("Downloaded painting photo matches expected image")
    public PaintingCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertDownloadedImageMatches(expected, paintingImage, "painting photo");
        return this;
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
