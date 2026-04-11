package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl();

    private final SelenideElement slogan = $("main").$(byText("Ваши любимые картины и художники всегда рядом"));
    private final SelenideElement paintingsCard = $("a[href='/painting']");
    private final SelenideElement artistsCard = $("a[href='/artist']");
    private final SelenideElement museumsCard = $("a[href='/museum']");
    private final ElementsCollection menuCards = $$("main nav ul li a");

    @Step("Open main page")
    public MainPage open() {
        Selenide.open(URL);
        return this;
    }

    @Override
    @Step("Check main page loaded")
    public MainPage checkPageLoaded() {
        super.checkPageLoaded();
        checkSlogan();
        checkCards();
        return this;
    }

    @Step("Check main page slogan")
    public MainPage checkSlogan() {
        slogan.shouldBe(visible);
        slogan.shouldHave(text("Ваши любимые картины и художники всегда рядом"));
        return this;
    }

    @Step("Check section cards are visible")
    public MainPage checkCards() {
        paintingsCard.shouldBe(visible);
        artistsCard.shouldBe(visible);
        museumsCard.shouldBe(visible);
        return this;
    }

    @Step("Open paintings page from main page")
    public PaintingPage openPaintingsPage() {
        paintingsCard.click();
        return new PaintingPage();
    }

    @Step("Open artists page from main page")
    public ArtistPage openArtistsPage() {
        artistsCard.click();
        return new ArtistPage();
    }

    @Step("Open museums page from main page")
    public MuseumPage openMuseumsPage() {
        museumsCard.click();
        return new MuseumPage();
    }
}