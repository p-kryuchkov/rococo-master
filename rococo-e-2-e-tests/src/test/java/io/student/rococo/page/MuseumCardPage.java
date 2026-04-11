package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class MuseumCardPage extends BasePage<MuseumCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement title = $("article.card header.card-header");
    private final SelenideElement location = $("article.card header.card-header + div");
    private final SelenideElement editBlock = $("article.card .w-56");
    private final SelenideElement description = $("article.card .w-56 + div");
    private final SelenideElement imagePreview = $("article.card img");

    @Step("Open museum card page by id: {museumId}")
    public MuseumCardPage openPage(String museumId) {
        open(CFG.frontUrl() + "museum/" + museumId);
        return this;
    }

    @Override
    @Step("Check museum card page loaded")
    public MuseumCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        title.shouldBe(visible);
        location.shouldBe(visible);
        imagePreview.shouldBe(visible);
        return this;
    }

    @Step("Check museum title: {museumTitle}")
    public MuseumCardPage checkTitle(String museumTitle) {
        title.shouldHave(text(museumTitle));
        return this;
    }

    @Step("Check museum location: {locationText}")
    public MuseumCardPage checkLocation(String locationText) {
        location.shouldHave(text(locationText));
        return this;
    }

    @Step("Check museum country and city")
    public MuseumCardPage checkLocation(String country, String city) {
        location.shouldHave(text(country + ", " + city));
        return this;
    }

    @Step("Check museum description: {descriptionText}")
    public MuseumCardPage checkDescription(String descriptionText) {
        description.shouldHave(text(descriptionText));
        return this;
    }

    @Step("Check museum image exists")
    public MuseumCardPage checkImageExists() {
        imagePreview.shouldBe(visible);
        return this;
    }

    @Step("Check museum image loaded")
    public MuseumCardPage checkImageLoaded() {
        imagePreview.shouldBe(visible).shouldHave(image);
        return this;
    }

    @Step("Check edit block exists")
    public MuseumCardPage checkEditBlockExists() {
        editBlock.should(exist);
        return this;
    }

    @Step("Check edit button is visible")
    public MuseumCardPage checkEditButtonVisible() {
        editBlock.$("button").shouldBe(visible);
        return this;
    }
}