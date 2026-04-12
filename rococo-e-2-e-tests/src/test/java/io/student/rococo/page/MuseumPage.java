package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.MuseumCreateModal;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL = CFG.frontUrl() + "museum";

    private final SelenideElement title = pageContent.$("h2");
    private final SelenideElement searchInput = pageContent.$("input[placeholder='Искать музей...']");
    private final SelenideElement museumsList = pageContent.$("ul");
    private final ElementsCollection museumCards = museumsList.$$("li");
    private final SelenideElement addMuseumButton = pageContent.$(byText("Добавить музей"));

    @Step("Open museums page")
    public MuseumPage open() {
        Selenide.open(URL);
        return this;
    }

    @Override
    @Step("Check museums page is loaded")
    public MuseumPage checkPageLoaded() {
        super.checkPageLoaded();
        title.shouldBe(visible).shouldHave(text("Музеи"));
        searchInput.shouldBe(visible);
        museumsList.shouldBe(visible);
        return this;
    }

    @Step("Search museum by value: {value}")
    public MuseumPage searchMuseum(String value) {
        searchInput.shouldBe(visible).setValue(value).pressEnter();
        return this;
    }

    @Step("Check museums are displayed")
    public MuseumPage checkMuseumsExist() {
        museumCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check museum '{museumTitle}' is displayed")
    public MuseumPage checkMuseumDisplayed(String museumTitle) {
        museumCard(museumTitle).shouldBe(visible);
        return this;
    }

    @Step("Open museum page for museum '{museumTitle}'")
    public MuseumCardPage openMuseumPage(String museumTitle) {
        museumCard(museumTitle)
                .$("a")
                .shouldBe(visible)
                .click();
        return new MuseumCardPage();
    }

    @Step("Check add museum button is displayed")
    public MuseumPage checkAddMuseumButtonDisplayed() {
        addMuseumButton.shouldBe(visible);
        return this;
    }

    @Step("Check add museum button is not displayed")
    public MuseumPage checkAddMuseumButtonNotDisplayed() {
        addMuseumButton.shouldNot(exist);
        return this;
    }

    @Step("Open create museum modal")
    public MuseumCreateModal openCreateMuseumModal() {
        addMuseumButton.shouldBe(visible).click();
        return new MuseumCreateModal();
    }

    private SelenideElement museumCard(String museumTitle) {
        return museumCards.findBy(text(museumTitle));
    }
}