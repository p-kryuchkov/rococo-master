package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.student.rococo.config.Config;
import io.student.rococo.page.component.Header;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();
    protected final SelenideElement pageContent = $("#page-content");

    @SuppressWarnings("unchecked")
    public T checkPageLoaded() {
        pageContent.shouldBe(visible);
        header.checkHeaderVisible();
        return (T) this;
    }

    public MainPage openMainPage() {
        return header.openMainPage();
    }

    public LoginPage openLoginPage() {
        return header.clickLogin();
    }

    public PaintingPage openPaintingsPageFromHeader() {
        return header.openPaintingsPage();
    }

    public ArtistPage openArtistsPageFromHeader() {
        return header.openArtistsPage();
    }

    public MuseumPage openMuseumsPageFromHeader() {
        return header.openMuseumsPage();
    }

    public ProfileCardPage openProfileCardPage() {
        return header.openProfilePage();
    }

    @SuppressWarnings("unchecked")
    public T toggleTheme() {
        header.toggleTheme();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T checkLoginButtonDoesNotExist() {
        header.checkLoginButtonDoesNotExist();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T checkLoginButtonExist() {
        header.checkLoginButtonExist();
        return (T) this;
    }
}