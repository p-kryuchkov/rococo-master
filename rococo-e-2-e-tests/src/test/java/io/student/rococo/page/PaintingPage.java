package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaintingPage extends BasePage<PaintingPage> {
    public static final String URL = CFG.frontUrl() + "painting";

    private final SelenideElement title = $("h2");
    private final SelenideElement searchInput = $("input[placeholder='Искать картины...']");
    private final SelenideElement searchButton = $("button.btn-icon");
    private final SelenideElement paintingsList = $("main ul");
    private final ElementsCollection paintingCards = $$("main ul li");
    private final ElementsCollection paintingLinks = $$("main ul li a");

    @Step("Open painting page")
    public PaintingPage open() {
        Selenide.open(URL);
        return this;
    }

    @Override
    @Step("Check painting page loaded")
    public PaintingPage checkPageLoaded() {
        super.checkPageLoaded();
        title.shouldBe(visible).shouldHave(text("Картины"));
        searchInput.shouldBe(visible);
        paintingsList.shouldBe(visible);
        return this;
    }

    @Step("Check painting page title")
    public PaintingPage checkTitle() {
        title.shouldHave(text("Картины"));
        return this;
    }

    @Step("Search painting by value {value}")
    public PaintingPage search(String value) {
        searchInput.shouldBe(visible).setValue(value);
        searchButton.click();
        return this;
    }

    @Step("Check painting with title {paintingTitle}")
    public PaintingPage checkPainting(String paintingTitle) {
        paintingCards.findBy(text(paintingTitle))
                .shouldBe(visible);
        return this;
    }

    @Step("Open painting with title {paintingTitle}")
    public PaintingPage openPainting(String paintingTitle) {
        paintingCards.findBy(text(paintingTitle))
                .shouldBe(visible)
                .$("a")
                .click();
        return this;
    }

    @Step("Check paintings list is not empty")
    public PaintingPage checkPaintingsExists() {
        paintingCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check painting details link exists")
    public PaintingPage checkPaintingLinkExists(String paintingTitle) {
        paintingCards.findBy(text(paintingTitle))
                .$("a[href*='/painting/']")
                .should(exist);
        return this;
    }
}