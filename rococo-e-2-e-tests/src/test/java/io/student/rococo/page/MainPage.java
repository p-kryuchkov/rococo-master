package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl();

    private final SelenideElement slogan = $("main").$(byText("Ваши любимые картины и художники всегда рядом"));
    private final SelenideElement paintingsCard = $("a[href='/painting']");
    private final SelenideElement artistsCard = $("a[href='/artist']");
    private final SelenideElement museumsCard = $("a[href='/museum']");

    @Override
    protected MainPage self() {
        return this;
    }

    @Step("Open main page")
    public MainPage open() {
        return openPage(URL);
    }

    @Override
    @Step("Check that main page is loaded")
    public MainPage checkPageLoaded() {
        super.checkPageLoaded();
        checkVisible(slogan, paintingsCard, artistsCard, museumsCard);
        return this;
    }

    @Step("Check that navigation cards are displayed")
    public MainPage checkNavigationCardsAreDisplayed() {
        checkVisible(paintingsCard, artistsCard, museumsCard);
        return this;
    }

    @Step("Open paintings page from main page card")
    public PaintingPage openPaintingsPageFromCard() {
        paintingsCard.shouldBe(visible).click();
        return new PaintingPage();
    }

    @Step("Open artists page from main page card")
    public ArtistPage openArtistsPageFromCard() {
        artistsCard.shouldBe(visible).click();
        return new ArtistPage();
    }

    @Step("Open museums page from main page card")
    public MuseumPage openMuseumsPageFromCard() {
        museumsCard.shouldBe(visible).click();
        return new MuseumPage();
    }
}