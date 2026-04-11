package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class PaintingCardPage extends BasePage<PaintingCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement contentGrid = card.$("div.grid");
    private final SelenideElement title = card.$("header.card-header");
    private final SelenideElement artistName = card.$("header.card-header + div");
    private final SelenideElement editBlock = card.$(".w-56");
    private final SelenideElement description = card.$(".m-4");
    private final SelenideElement imagePreview = card.$("img");

    @Step("Open painting card page by id: {paintingId}")
    public PaintingCardPage openPage(String paintingId) {
        open(CFG.frontUrl() + "painting/" + paintingId);
        return this;
    }

    @Override
    @Step("Check painting card page loaded")
    public PaintingCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        contentGrid.shouldBe(visible);
        title.shouldBe(visible);
        artistName.shouldBe(visible);
        imagePreview.shouldBe(visible);
        return this;
    }

    @Step("Check painting title: {paintingTitle}")
    public PaintingCardPage checkTitle(String paintingTitle) {
        title.shouldHave(text(paintingTitle));
        return this;
    }

    @Step("Check painting artist: {artist}")
    public PaintingCardPage checkArtist(String artist) {
        artistName.shouldHave(text(artist));
        return this;
    }

    @Step("Check painting description: {descriptionText}")
    public PaintingCardPage checkDescription(String descriptionText) {
        description.shouldHave(text(descriptionText));
        return this;
    }

    @Step("Check painting image exists")
    public PaintingCardPage checkImageExists() {
        imagePreview.should(exist);
        return this;
    }

    @Step("Check painting image loaded")
    public PaintingCardPage checkImageLoaded() {
        imagePreview.shouldBe(visible).shouldHave(image);
        return this;
    }

    @Step("Check edit block exists")
    public PaintingCardPage checkEditBlockExists() {
        editBlock.should(exist);
        return this;
    }

    @Step("Check edit button is visible")
    public PaintingCardPage checkEditButtonVisible() {
        editBlock.$("button").shouldBe(visible);
        return this;
    }
}