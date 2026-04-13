package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.MuseumCreateModal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL = CFG.frontUrl() + "museum";

    private final SelenideElement title = pageContent.$("h2");
    private final SelenideElement searchInput = pageContent.$("input[placeholder='Искать музей...']");
    private final SelenideElement museumsList = pageContent.$("ul");
    private final ElementsCollection museumCards = museumsList.$$("li");
    private final SelenideElement addMuseumButton = pageContent.$(byText("Добавить музей"));

    @Override
    protected MuseumPage self() {
        return this;
    }

    @Step("Open museums page")
    public MuseumPage open() {
        return openPage(URL);
    }

    @Override
    @Step("Check museums page is loaded")
    public MuseumPage checkPageLoaded() {
        super.checkPageLoaded();
        checkTitle(title, "Музеи");
        checkVisible(searchInput, museumsList);
        return this;
    }

    @Step("Museums page is opened for unauthorized user")
    public MuseumPage checkOpenedForUnauthorizedUser() {
        return checkPageLoaded()
                .checkMuseumsExist()
                .checkAddMuseumButtonNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Step("Museums page is opened for authorized user")
    public MuseumPage checkOpenedForAuthorizedUser() {
        return checkPageLoaded()
                .checkMuseumsExist()
                .checkAddMuseumButtonDisplayed()
                .checkLoginButtonIsNotDisplayed();
    }

    @Step("Search museum by value: {value}")
    public MuseumPage searchMuseum(String value) {
        search(searchInput, value);
        return this;
    }

    @Step("Search museum by value and check it is displayed: {value}")
    public MuseumPage searchAndCheckMuseumDisplayed(String value) {
        return searchMuseum(value).checkMuseumDisplayed(value);
    }

    @Step("Check museums are displayed")
    public MuseumPage checkMuseumsExist() {
        checkCardsExist(museumCards);
        return this;
    }

    @Step("Check museum '{museumTitle}' is displayed")
    public MuseumPage checkMuseumDisplayed(String museumTitle) {
        cardByText(museumCards, museumTitle);
        return this;
    }

    @Step("Open museum page for museum '{museumTitle}'")
    public MuseumCardPage openMuseumPage(String museumTitle) {
        openCardByText(museumCards, museumTitle);
        return new MuseumCardPage();
    }

    @Step("Search museum and open museum page: {museumTitle}")
    public MuseumCardPage openMuseumCard(String museumTitle) {
        return searchAndCheckMuseumDisplayed(museumTitle)
                .openMuseumPage(museumTitle)
                .checkPageLoaded();
    }

    @Step("Check add museum button is displayed")
    public MuseumPage checkAddMuseumButtonDisplayed() {
        checkVisible(addMuseumButton);
        return this;
    }

    @Step("Check add museum button is not displayed")
    public MuseumPage checkAddMuseumButtonNotDisplayed() {
        checkDoesNotExist(addMuseumButton);
        return this;
    }

    @Step("Open create museum modal")
    public MuseumCreateModal openCreateMuseumModal() {
        addMuseumButton.shouldBe(visible).click();
        return new MuseumCreateModal();
    }
}
