package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ArtistCardPage extends BasePage<ArtistCardPage> {

    private final SelenideElement card = $("article.card");
    private final SelenideElement title = $("article.card header.card-header");
    private final SelenideElement contentSection = $("article.card section");
    private final SelenideElement artistInfo = $("article.card section .flex.flex-col");
    private final SelenideElement avatar = artistInfo.$("figure[data-testid='avatar']");
    private final SelenideElement avatarImage = artistInfo.$("figure[data-testid='avatar'] img");
    private final SelenideElement editBlock = artistInfo.$(".w-56");
    private final ElementsCollection infoBlocks = artistInfo.$$(":scope > div");

    @Step("Open artist card page by id: {artistId}")
    public ArtistCardPage openPage(String artistId) {
        open(CFG.frontUrl() + "artist/" + artistId);
        return this;
    }

    @Override
    @Step("Check artist card page loaded")
    public ArtistCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        title.shouldBe(visible);
        contentSection.shouldBe(visible);
        avatar.shouldBe(visible);
        return this;
    }

    @Step("Check artist title: {artistName}")
    public ArtistCardPage checkTitle(String artistName) {
        title.shouldHave(text(artistName));
        return this;
    }

    @Step("Check artist avatar exists")
    public ArtistCardPage checkAvatarExists() {
        avatar.should(exist);
        return this;
    }

    @Step("Check artist avatar image loaded")
    public ArtistCardPage checkAvatarLoaded() {
        avatarImage.shouldBe(visible).shouldHave(image);
        return this;
    }

    @Step("Check edit block exists")
    public ArtistCardPage checkEditBlockExists() {
        editBlock.should(exist);
        return this;
    }

    @Step("Check edit button is visible")
    public ArtistCardPage checkEditButtonVisible() {
        editBlock.$("button").shouldBe(visible);
        return this;
    }

    @Step("Check artist biography: {biographyText}")
    public ArtistCardPage checkBiography(String biographyText) {
        infoBlocks.last().shouldHave(text(biographyText));
        return this;
    }
}