package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL = CFG.frontUrl() + "museum";

    private final SelenideElement title = $("h2");
    private final SelenideElement searchInput = $("input[placeholder='Искать музей...']");
    private final SelenideElement searchButton = $("button.btn-icon");
    private final SelenideElement museumsList = $("main ul");
    private final ElementsCollection museumCards = $$("main ul li");

    @Step("Open museum page")
    public MuseumPage openPage() {
        open(URL);
        return this;
    }

    @Override
    @Step("Check museum page loaded")
    public MuseumPage checkPageLoaded() {
        super.checkPageLoaded();
        title.shouldBe(visible).shouldHave(text("Музеи"));
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible);
        museumsList.shouldBe(visible);
        return this;
    }

    @Step("Check museum page title")
    public MuseumPage checkTitle() {
        title.shouldHave(text("Музеи"));
        return this;
    }

    @Step("Search museum by value: {value}")
    public MuseumPage search(String value) {
        searchInput.shouldBe(visible).setValue(value);
        searchButton.click();
        return this;
    }

    @Step("Check museums list is not empty")
    public MuseumPage checkMuseumsExist() {
        museumCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check museum with name: {museumName}")
    public MuseumPage checkMuseum(String museumName) {
        museumCards.findBy(text(museumName))
                .shouldBe(visible);
        return this;
    }

    @Step("Open museum with name: {museumName}")
    public MuseumPage openMuseum(String museumName) {
        museumCards.findBy(text(museumName))
                .shouldBe(visible)
                .$("a[href*='/museum/']")
                .click();
        return this;
    }

    @Step("Check museum link exists for: {museumName}")
    public MuseumPage checkMuseumLinkExists(String museumName) {
        museumCards.findBy(text(museumName))
                .$("a[href*='/museum/']")
                .should(exist);
        return this;
    }

    @Step("Check museum location: {location}")
    public MuseumPage checkMuseumLocation(String museumName, String location) {
        museumCards.findBy(text(museumName))
                .shouldHave(text(location));
        return this;
    }
}