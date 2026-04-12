package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.config.Config;
import io.student.rococo.page.component.Header;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();
    protected final SelenideElement pageContent = $("#page-content");

    @SuppressWarnings("unchecked")
    @Step("Check that page is loaded")
    public T checkPageLoaded() {
        pageContent.shouldBe(visible);
        header.checkHeaderVisible();
        return (T) this;
    }

    @Step("Open main page from header")
    public MainPage openMainPageFromHeader() {
        return header.openMainPage();
    }

    @Step("Open login page from header")
    public LoginPage openLoginPageFromHeader() {
        return header.clickLogin();
    }

    @Step("Open paintings page from header")
    public PaintingPage openPaintingsPageFromHeader() {
        return header.openPaintingsPage();
    }

    @Step("Open artists page from header")
    public ArtistPage openArtistsPageFromHeader() {
        return header.openArtistsPage();
    }

    @Step("Open museums page from header")
    public MuseumPage openMuseumsPageFromHeader() {
        return header.openMuseumsPage();
    }

    @Step("Open profile page from header")
    public ProfileCardPage openProfilePageFromHeader() {
        return header.openProfilePage();
    }

    @SuppressWarnings("unchecked")
    @Step("Toggle theme")
    public T toggleTheme() {
        header.toggleTheme();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Step("Check that login button is not displayed")
    public T checkLoginButtonIsNotDisplayed() {
        header.checkLoginButtonDoesNotExist();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Step("Check that login button is displayed")
    public T checkLoginButtonIsDisplayed() {
        header.checkLoginButtonExist();
        return (T) this;
    }

    protected BufferedImage readImageBySrc(String src) throws IOException {
        if (src.startsWith("data:image")) {
            String base64 = src.substring(src.indexOf(',') + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        }

        return ImageIO.read(URI.create(src).toURL());
    }
}