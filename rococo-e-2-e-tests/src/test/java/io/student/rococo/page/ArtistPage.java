package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.ArtistCreateModal;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;

public class ArtistPage extends BasePage<ArtistPage> {
    public static final String URL = CFG.frontUrl() + "artist";

    private final SelenideElement title = pageContent.$("h2");
    private final SelenideElement searchInput = pageContent.$("input[placeholder='Искать художников...']");
    private final SelenideElement artistsList = pageContent.$("ul");
    private final ElementsCollection artistCards = artistsList.$$("li");
    private final SelenideElement addArtistButton = pageContent.$(byText("Добавить художника"));

    @Step("Open artists page")
    public ArtistPage open() {
        Selenide.open(URL);
        return this;
    }

    @Override
    @Step("Check artists page is loaded")
    public ArtistPage checkPageLoaded() {
        super.checkPageLoaded();
        title.shouldBe(visible).shouldHave(text("Художники"));
        searchInput.shouldBe(visible);
        artistsList.shouldBe(visible);
        return this;
    }

    @Step("Search artist by value: {value}")
    public ArtistPage searchArtist(String value) {
        searchInput.shouldBe(visible).setValue(value).pressEnter();
        return this;
    }

    @Step("Check artists are displayed")
    public ArtistPage checkArtistsExist() {
        artistCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check artist '{artistName}' is displayed")
    public ArtistPage checkArtistDisplayed(String artistName) {
        artistCard(artistName).shouldBe(visible);
        return this;
    }

    @Step("Open artist page for artist '{artistName}'")
    public ArtistCardPage openArtistPage(String artistName) {
        artistCard(artistName)
                .$("a")
                .shouldBe(visible)
                .click();
        return new ArtistCardPage();
    }

    @Step("Check add artist button is displayed")
    public ArtistPage checkAddArtistButtonDisplayed() {
        addArtistButton.shouldBe(visible);
        return this;
    }

    @Step("Check add artist button is not displayed")
    public ArtistPage checkAddArtistButtonNotDisplayed() {
        addArtistButton.shouldNot(exist);
        return this;
    }

    @Step("Open create artist modal")
    public ArtistCreateModal openCreateArtistModal() {
        addArtistButton.shouldBe(visible).click();
        return new ArtistCreateModal();
    }

    private SelenideElement artistCard(String artistName) {
        return artistCards.findBy(text(artistName));
    }
}