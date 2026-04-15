package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.MuseumUpdateModal;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MuseumCardPage extends BasePage<MuseumCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement museumInfo = card.$(".grid > div");
    private final SelenideElement museumName = museumInfo.$("header.card-header");
    private final SelenideElement museumLocation = museumInfo.$("header.card-header + div");
    private final SelenideElement editMuseumButton = museumInfo.$("header.card-header + div + div button");
    private final SelenideElement museumDescription = museumInfo.$("header.card-header + div + div + div");
    private final SelenideElement museumImage = card.$("img");

    @Override
    protected MuseumCardPage self() {
        return this;
    }

    @Step("Open museum card page by id: {museumId}")
    public MuseumCardPage open(String museumId) {
        return openPage(CFG.frontUrl() + "museum/" + museumId);
    }

    @Override
    @Step("Check museum card page is loaded")
    public MuseumCardPage checkPageLoaded() {
        super.checkPageLoaded();
        checkVisible(card, museumName, museumLocation);
        return this;
    }

    @Step("Check museum page opened for authorized user")
    public MuseumCardPage checkOpenedForAuthorizedUser(String title) {
        return checkPageLoaded()
                .checkMuseumNameIsDisplayed(title)
                .checkEditMuseumButtonIsDisplayed()
                .checkLoginButtonIsNotDisplayed();
    }

    @Step("Check museum page opened for unauthorized user")
    public MuseumCardPage checkOpenedForUnauthorizedUser(String title) {
        return checkPageLoaded()
                .checkMuseumNameIsDisplayed(title)
                .checkEditMuseumButtonIsNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Step("Check museum name '{museumName}' is displayed")
    public MuseumCardPage checkMuseumNameIsDisplayed(String museumName) {
        checkTitle(this.museumName, museumName);
        return this;
    }

    @Step("Check museum location '{location}' is displayed")
    public MuseumCardPage checkMuseumLocationIsDisplayed(String location) {
        museumLocation.shouldBe(visible).shouldHave(text(location));
        return this;
    }

    @Step("Check museum country '{country}' and city '{city}' are displayed")
    public MuseumCardPage checkMuseumLocationIsDisplayed(String country, String city) {
        return checkMuseumLocationIsDisplayed(country + ", " + city);
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
        checkVisible(editMuseumButton);
        return this;
    }

    @Step("Check edit museum button is not displayed")
    public MuseumCardPage checkEditMuseumButtonIsNotDisplayed() {
        checkDoesNotExist(editMuseumButton);
        return this;
    }

    @Step("Open edit museum form")
    public MuseumUpdateModal openEditMuseumForm() {
        editMuseumButton.shouldBe(visible).click();
        return new MuseumUpdateModal().checkModalOpened();
    }

    @Step("Edit museum")
    public MuseumCardPage editMuseum(String title, String country, String city, String description, BufferedImage photo) {
        openEditMuseumForm().updateMuseum(title, country, city, description, photo);
        return checkPageLoaded();
    }

    @Step("Museum photo screenshots match")
    public MuseumCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        assertScreenshotMatches(expected, museumImage, "museum photo");
        return this;
    }

    @Step("Downloaded museum photo matches expected image")
    public MuseumCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertDownloadedImageMatches(expected, museumImage, "museum photo");
        return this;
    }
}
