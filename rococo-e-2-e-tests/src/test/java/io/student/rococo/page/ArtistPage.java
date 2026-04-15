package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.ArtistCreateModal;

import static com.codeborne.selenide.Selectors.byText;

public class ArtistPage extends BasePage<ArtistPage> {
    public static final String URL = CFG.frontUrl() + "artist";

    private final SelenideElement title = pageContent.$("h2");
    private final SelenideElement searchInput = pageContent.$("input[placeholder='Искать художников...']");
    private final SelenideElement artistsList = pageContent.$("ul");
    private final ElementsCollection artistCards = artistsList.$$("li");
    private final SelenideElement addArtistButton = pageContent.$(byText("Добавить художника"));

    @Override
    protected ArtistPage self() {
        return this;
    }

    @Step("Open artists page")
    public ArtistPage open() {
        return openPage(URL);
    }

    @Override
    @Step("Check artists page is loaded")
    public ArtistPage checkPageLoaded() {
        super.checkPageLoaded();
        checkTitle(title, "Художники");
        checkVisible(searchInput, artistsList);
        return this;
    }

    @Step("Artists page is opened for unauthorized user")
    public ArtistPage checkOpenedForUnauthorizedUser() {
        return checkPageLoaded()
                .checkArtistsExist()
                .checkAddArtistButtonNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Step("Artists page is opened for authorized user")
    public ArtistPage checkOpenedForAuthorizedUser() {
        return checkPageLoaded()
                .checkArtistsExist()
                .checkAddArtistButtonDisplayed()
                .checkLoginButtonIsNotDisplayed();
    }

    @Step("Search artist by value: {value}")
    public ArtistPage searchArtist(String value) {
        search(searchInput, value);
        return this;
    }

    @Step("Search artist by value and check it is displayed: {value}")
    public ArtistPage searchAndCheckArtistDisplayed(String value) {
        return searchArtist(value).checkArtistDisplayed(value);
    }

    @Step("Check artists are displayed")
    public ArtistPage checkArtistsExist() {
        checkCardsExist(artistCards);
        return this;
    }

    @Step("Check artist '{artistName}' is displayed")
    public ArtistPage checkArtistDisplayed(String artistName) {
        cardByText(artistCards, artistName);
        return this;
    }

    @Step("Open artist page for artist '{artistName}'")
    public ArtistCardPage openArtistPage(String artistName) {
        openCardByText(artistCards, artistName);
        return new ArtistCardPage();
    }

    @Step("Search artist and open artist page: {artistName}")
    public ArtistCardPage openArtistCard(String artistName) {
        return searchAndCheckArtistDisplayed(artistName)
                .openArtistPage(artistName)
                .checkPageLoaded();
    }

    @Step("Check add artist button is displayed")
    public ArtistPage checkAddArtistButtonDisplayed() {
        checkVisible(addArtistButton);
        return this;
    }

    @Step("Check add artist button is not displayed")
    public ArtistPage checkAddArtistButtonNotDisplayed() {
        checkDoesNotExist(addArtistButton);
        return this;
    }

    @Step("Open create artist modal")
    public ArtistCreateModal openCreateArtistModal() {
        addArtistButton.shouldBe(com.codeborne.selenide.Condition.visible).click();
        return new ArtistCreateModal();
    }
}
