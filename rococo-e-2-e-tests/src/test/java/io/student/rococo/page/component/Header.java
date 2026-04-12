package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Header {
    private final SelenideElement appBar = $("[data-testid='app-bar']");
    private final SelenideElement mainLink = $("h1 a[href='/']");
    private final SelenideElement paintingsLink = $("a[href='/painting']");
    private final SelenideElement artistsLink = $("a[href='/artist']");
    private final SelenideElement museumsLink = $("a[href='/museum']");
    private final SelenideElement loginButton = $$("button").findBy(text("Войти"));
    private final SelenideElement themeSwitch = $("[role='switch']");
    private final SelenideElement profileAvatar = $("header [data-testid='avatar']");


    @Step("Check header is visible")
    public Header checkHeaderVisible() {
        appBar.shouldBe(visible);
        return this;
    }

    @Step("Open main page from header")
    public MainPage openMainPage() {
        mainLink.click();
        return new MainPage();
    }

    @Step("Open paintings page from header")
    public PaintingPage openPaintingsPage() {
        paintingsLink.click();
        return new PaintingPage();
    }

    @Step("Open artists page from header")
    public ArtistPage openArtistsPage() {
        artistsLink.click();
        return new ArtistPage();
    }

    @Step("Open museums page from header")
    public MuseumPage openMuseumsPage() {
        museumsLink.click();
        return new MuseumPage();
    }

    @Step("Click login button")
    public LoginPage clickLogin() {
        loginButton.shouldBe(visible).click();
        return new LoginPage();
    }

    @Step("Check login button does not exist")
    public Header checkLoginButtonDoesNotExist() {
        loginButton.shouldNotBe(exist);
        return this;
    }

    @Step("Check login button does not exist")
    public Header checkLoginButtonExist() {
        loginButton.shouldBe(exist);
        return this;
    }

    @Step("Toggle theme")
    public Header toggleTheme() {
        themeSwitch.shouldBe(visible).click();
        return this;
    }

    @Step("Open profile card")
    public ProfileCardPage openProfilePage() {
        profileAvatar.shouldBe(visible).click();
        return new ProfileCardPage();
    }
}