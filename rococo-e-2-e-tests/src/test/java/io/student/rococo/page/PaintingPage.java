package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.component.PaintingCreateModal;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;

public class PaintingPage extends BasePage<PaintingPage> {
    public static final String URL = CFG.frontUrl() + "painting";

    private final SelenideElement title = pageContent.$("h2");
    private final SelenideElement searchInput = pageContent.$("input[placeholder='Искать картины...']");
    private final SelenideElement paintingsList = pageContent.$("ul");
    private final ElementsCollection paintingCards = paintingsList.$$("li");
    private final SelenideElement addPaintingButton = pageContent.$(byText("Добавить картину"));

    @Step("Open paintings page")
    public PaintingPage open() {
        Selenide.open(URL);
        return this;
    }

    @Override
    @Step("Check paintings page is loaded")
    public PaintingPage checkPageLoaded() {
        super.checkPageLoaded();
        title.shouldBe(visible).shouldHave(text("Картины"));
        searchInput.shouldBe(visible);
        paintingsList.shouldBe(visible);
        return this;
    }

    @Step("Search painting by value: {value}")
    public PaintingPage searchPainting(String value) {
        searchInput.shouldBe(visible).setValue(value).pressEnter();
        return this;
    }

    @Step("Check paintings are displayed")
    public PaintingPage checkPaintingsExist() {
        paintingCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check painting '{paintingTitle}' is displayed")
    public PaintingPage checkPaintingDisplayed(String paintingTitle) {
        paintingCard(paintingTitle).shouldBe(visible);
        return this;
    }

    @Step("Open painting page for painting '{paintingTitle}'")
    public PaintingCardPage openPaintingPage(String paintingTitle) {
        paintingCard(paintingTitle)
                .$("a")
                .shouldBe(visible)
                .click();
        return new PaintingCardPage();
    }

    @Step("Check add painting button is displayed")
    public PaintingPage checkAddPaintingButtonDisplayed() {
        addPaintingButton.shouldBe(visible);
        return this;
    }

    @Step("Check add painting button is not displayed")
    public PaintingPage checkAddPaintingButtonNotDisplayed() {
        addPaintingButton.shouldNot(exist);
        return this;
    }

    @Step("Open create painting modal")
    public PaintingCreateModal openCreatePaintingModal() {
        addPaintingButton.shouldBe(visible).click();
        return new PaintingCreateModal();
    }

    @Step("Check painting details link exists: {paintingTitle}")
    public PaintingPage checkPaintingLinkExists(String paintingTitle) {
        paintingCard(paintingTitle)
                .$("a[href*='/painting/']")
                .should(exist);
        return this;
    }

    private SelenideElement paintingCard(String paintingTitle) {
        return paintingCards.findBy(text(paintingTitle));
    }

    @Step("Painting page is opened for unauthorized user")
    public PaintingPage checkOpenedForUnauthorizedUser() {
        return checkPageLoaded()
                .checkPaintingsExist()
                .checkAddPaintingButtonNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Step("Painting page is opened for authorized user")
    public PaintingPage checkOpenedForAuthorizedUser() {
        return checkPageLoaded()
                .checkAddPaintingButtonDisplayed()
                .checkLoginButtonIsNotDisplayed();
    }

    @Step("Search painting by title: {title}")
    public PaintingPage searchAndCheckPaintingDisplayed(String title) {
        return searchPainting(title)
                .checkPaintingDisplayed(title);
    }

    @Step("Open painting card by title: {title}")
    public PaintingCardPage openPaintingCard(String title) {
        return searchAndCheckPaintingDisplayed(title)
                .openPaintingPage(title)
                .checkPageLoaded();
    }

    @Step("Create painting and open created card")
    public PaintingCardPage createPaintingAndOpenCard(String title,
                                                      String artist,
                                                      String museum,
                                                      BufferedImage image,
                                                      String description) {
        openCreatePaintingModal()
                .checkModalLoadedWithArtistSelect()
                .createPainting(title, artist, museum, image, description)
                .checkModalClosed();

        return openPaintingCard(title);
    }
}