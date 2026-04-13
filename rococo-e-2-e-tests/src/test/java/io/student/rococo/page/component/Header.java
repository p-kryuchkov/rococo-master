package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.ArtistPage;
import io.student.rococo.page.LoginPage;
import io.student.rococo.page.MainPage;
import io.student.rococo.page.MuseumPage;
import io.student.rococo.page.PaintingPage;
import io.student.rococo.page.ProfileCardPage;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
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
    public Header checkVisible() {
        appBar.shouldBe(visible);
        return this;
    }

    @Step("Open main page from header")
    public MainPage openMainPage() {
        mainLink.shouldBe(visible).click();
        return new MainPage();
    }

    @Step("Open paintings page from header")
    public PaintingPage openPaintingsPage() {
        paintingsLink.shouldBe(visible).click();
        return new PaintingPage();
    }

    @Step("Open artists page from header")
    public ArtistPage openArtistsPage() {
        artistsLink.shouldBe(visible).click();
        return new ArtistPage();
    }

    @Step("Open museums page from header")
    public MuseumPage openMuseumsPage() {
        museumsLink.shouldBe(visible).click();
        return new MuseumPage();
    }

    @Step("Open login page")
    public LoginPage openLoginPage() {
        loginButton.shouldBe(visible).click();
        return new LoginPage();
    }

    @Step("Check login button is not displayed")
    public Header checkLoginButtonIsNotDisplayed() {
        loginButton.shouldNot(exist);
        return this;
    }

    @Step("Check login button is displayed")
    public Header checkLoginButtonIsDisplayed() {
        loginButton.shouldBe(visible);
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
