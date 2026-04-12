package io.student.rococo.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl();

    private final SelenideElement slogan = $("main").$(byText("Ваши любимые картины и художники всегда рядом"));
    private final SelenideElement paintingsCard = $("a[href='/painting']");
    private final SelenideElement artistsCard = $("a[href='/artist']");
    private final SelenideElement museumsCard = $("a[href='/museum']");

    @Step("Open main page")
    public MainPage open() {
        Selenide.open(URL);
        return this;
    }

    @Override
    @Step("Check that main page is loaded")
    public MainPage checkPageLoaded() {
        super.checkPageLoaded();
        checkSloganIsDisplayed();
        checkNavigationCardsAreDisplayed();
        return this;
    }

    @Step("Check that main page slogan is displayed")
    public MainPage checkSloganIsDisplayed() {
        slogan.shouldBe(visible);
        slogan.shouldHave(text("Ваши любимые картины и художники всегда рядом"));
        return this;
    }

    @Step("Check that navigation cards are displayed")
    public MainPage checkNavigationCardsAreDisplayed() {
        paintingsCard.shouldBe(visible);
        artistsCard.shouldBe(visible);
        museumsCard.shouldBe(visible);
        return this;
    }

    @Step("Open paintings page from main page card")
    public PaintingPage openPaintingsPageFromCard() {
        paintingsCard.click();
        return new PaintingPage();
    }

    @Step("Open artists page from main page card")
    public ArtistPage openArtistsPageFromCard() {
        artistsCard.click();
        return new ArtistPage();
    }

    @Step("Open museums page from main page card")
    public MuseumPage openMuseumsPageFromCard() {
        museumsCard.click();
        return new MuseumPage();
    }
}