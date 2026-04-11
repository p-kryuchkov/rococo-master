package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ArtistPage extends BasePage<ArtistPage> {
    public static final String URL = CFG.frontUrl() + "artist";

    private final SelenideElement title = $("h2");
    private final SelenideElement searchInput = $("input[placeholder='Искать художников...']");
    private final SelenideElement searchButton = $("button.btn-icon");
    private final SelenideElement artistsList = $("main ul");
    private final ElementsCollection artistCards = $$("main ul li");

    @Step("Open artist page")
    public ArtistPage openPage() {
        open(URL);
        return this;
    }

    @Override
    @Step("Check artist page loaded")
    public ArtistPage checkPageLoaded() {
        super.checkPageLoaded();
        title.shouldBe(visible).shouldHave(text("Художники"));
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible);
        artistsList.shouldBe(visible);
        return this;
    }

    @Step("Check artist page title")
    public ArtistPage checkTitle() {
        title.shouldHave(text("Художники"));
        return this;
    }

    @Step("Search artist by value: {value}")
    public ArtistPage search(String value) {
        searchInput.shouldBe(visible).setValue(value);
        searchButton.click();
        return this;
    }

    @Step("Check artists list is not empty")
    public ArtistPage checkArtistsExist() {
        artistCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check artist with name: {artistName}")
    public ArtistPage checkArtist(String artistName) {
        artistCards.findBy(text(artistName))
                .shouldBe(visible);
        return this;
    }

    @Step("Open artist with name: {artistName}")
    public ArtistPage openArtist(String artistName) {
        artistCards.findBy(text(artistName))
                .shouldBe(visible)
                .$("a[href*='/artist/']")
                .click();
        return this;
    }

    @Step("Check artist link exists for: {artistName}")
    public ArtistPage checkArtistLinkExists(String artistName) {
        artistCards.findBy(text(artistName))
                .$("a[href*='/artist/']")
                .should(exist);
        return this;
    }

    @Step("Check artist avatar exists for: {artistName}")
    public ArtistPage checkArtistAvatarExists(String artistName) {
        artistCards.findBy(text(artistName))
                .$("figure[data-testid='avatar']")
                .should(exist);
        return this;
    }
}