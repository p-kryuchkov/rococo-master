package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.MuseumUpdateModal;
import io.student.rococo.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MuseumCardPage extends BasePage<MuseumCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement museumInfo = card.$(".grid > div");
    private final SelenideElement museumName = museumInfo.$("header.card-header");
    private final SelenideElement museumLocation = museumInfo.$("header.card-header + div");
    private final SelenideElement editMuseumBlock = museumInfo.$("header.card-header + div + div");
    private final SelenideElement museumDescription = museumInfo.$("header.card-header + div + div + div");
    private final SelenideElement museumImage = card.$("img");

    private final MuseumUpdateModal museumUpdateModal = new MuseumUpdateModal();

    @Step("Open museum card page by id: {museumId}")
    public MuseumCardPage open(String museumId) {
        open(CFG.frontUrl() + "museum/" + museumId);
        return this;
    }

    @Override
    @Step("Check museum card page is loaded")
    public MuseumCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        museumName.shouldBe(visible);
        museumLocation.shouldBe(visible);
        return this;
    }

    @Step("Check museum name '{museumName}' is displayed")
    public MuseumCardPage checkMuseumNameIsDisplayed(String museumName) {
        this.museumName.shouldBe(visible).shouldHave(text(museumName));
        return this;
    }

    @Step("Check museum location '{location}' is displayed")
    public MuseumCardPage checkMuseumLocationIsDisplayed(String location) {
        museumLocation.shouldBe(visible).shouldHave(text(location));
        return this;
    }

    @Step("Check museum country '{country}' and city '{city}' are displayed")
    public MuseumCardPage checkMuseumLocationIsDisplayed(String country, String city) {
        museumLocation.shouldBe(visible).shouldHave(text(country + ", " + city));
        return this;
    }

    @Step("Check museum description is displayed")
    public MuseumCardPage checkMuseumDescriptionIsDisplayed(String description) {
        museumDescription.shouldBe(visible).shouldHave(text(description));
        return this;
    }

    @Step("Check museum image is displayed")
    public MuseumCardPage checkMuseumImageIsDisplayed() {
        museumImage.shouldBe(visible);
        return this;
    }

    @Step("Check museum image is loaded")
    public MuseumCardPage checkMuseumImageIsLoaded() {
        museumImage.shouldBe(visible).shouldHave(image);
        return this;
    }

    @Step("Check edit museum button is displayed")
    public MuseumCardPage checkEditMuseumButtonIsDisplayed() {
        editMuseumBlock.$("button").shouldBe(visible);
        return this;
    }

    @Step("Check edit museum button is not displayed")
    public MuseumCardPage checkEditMuseumButtonIsNotDisplayed() {
        editMuseumBlock.$("button").shouldNot(exist);
        return this;
    }

    @Step("Open edit museum form")
    public MuseumCardPage openEditMuseumForm() {
        editMuseumBlock.$("button").shouldBe(visible).click();
        museumUpdateModal.checkModalOpened();
        return this;
    }

    @Step("Edit museum with title '{title}'")
    public MuseumCardPage editMuseum(String title, String country, String city, String description, BufferedImage photo) {
        museumUpdateModal.updateMuseum(title, country, city, description, photo);
        return this;
    }

    @Step("Screenshot museum photo")
    public File screenshotMuseumPhoto() {
        return museumImage.screenshot();
    }

    @Step("Museum photo screenshots match")
    public MuseumCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        try {
            assertFalse(
                    new ScreenDiffResult(expected, ImageIO.read(screenshotMuseumPhoto())),
                    "Screen comparison failure"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Step("Download museum photo")
    public BufferedImage downloadMuseumPhoto() {
        try {
            String src = museumImage
                    .shouldBe(visible)
                    .getAttribute("src");

            if (src == null || src.isBlank()) {
                throw new IllegalStateException("Museum photo src is empty");
            }

            return readImageBySrc(src);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download museum photo", e);
        }
    }

    @Step("Downloaded museum photo matches expected image")
    public MuseumCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertFalse(
                new ScreenDiffResult(expected, downloadMuseumPhoto()),
                "Screen comparison failure"
        );
        return this;
    }
}