package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.config.Config;
import io.student.rococo.page.component.Header;
import io.student.rococo.utils.ScreenDiffResult;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<T>> {
    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();
    protected final SelenideElement pageContent = $("#page-content");

    protected abstract T self();

    @Step("Open page: {url}")
    protected T openPage(String url) {
        Selenide.open(url);
        return self();
    }

    @Step("Check that page is loaded")
    public T checkPageLoaded() {
        pageContent.shouldBe(visible);
        header.checkVisible();
        return self();
    }

    @Step("Open main page from header")
    public MainPage openMainPageFromHeader() {
        return header.openMainPage();
    }

    @Step("Open login page from header")
    public LoginPage openLoginPageFromHeader() {
        return header.openLoginPage();
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

    @Step("Toggle theme")
    public T toggleTheme() {
        header.toggleTheme();
        return self();
    }

    @Step("Check that login button is not displayed")
    public T checkLoginButtonIsNotDisplayed() {
        header.checkLoginButtonIsNotDisplayed();
        return self();
    }

    @Step("Check that login button is displayed")
    public T checkLoginButtonIsDisplayed() {
        header.checkLoginButtonIsDisplayed();
        return self();
    }

    protected void checkTitle(SelenideElement title, String expectedText) {
        title.shouldBe(visible).shouldHave(text(expectedText));
    }

    protected void checkVisible(SelenideElement... elements) {
        for (SelenideElement element : elements) {
            element.shouldBe(visible);
        }
    }

    protected void checkExists(SelenideElement element) {
        element.shouldBe(exist);
    }

    protected void checkDoesNotExist(SelenideElement element) {
        element.shouldNot(exist);
    }

    protected void search(SelenideElement input, String value) {
        input.shouldBe(visible).setValue(value).pressEnter();
    }

    protected void checkCardsExist(ElementsCollection cards) {
        cards.shouldHave(sizeGreaterThan(0));
    }

    protected SelenideElement cardByText(ElementsCollection cards, String value) {
        return cards.findBy(text(value)).shouldBe(visible);
    }

    protected void openCardByText(ElementsCollection cards, String value) {
        cardByText(cards, value).$("a").shouldBe(visible).click();
    }

    protected BufferedImage readImage(SelenideElement image, String imageName) {
        try {
            String src = image.shouldBe(visible).getAttribute("src");
            if (src == null || src.isBlank()) {
                throw new IllegalStateException(imageName + " src is empty");
            }
            return readImageBySrc(src);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download " + imageName, e);
        }
    }

    protected File screenshot(SelenideElement imageElement) {
        return imageElement.screenshot();
    }

    protected void assertScreenshotMatches(BufferedImage expected, SelenideElement imageElement, String imageName) {
        try {
            assertFalse(
                    new ScreenDiffResult(expected, ImageIO.read(screenshot(imageElement))),
                    imageName + " screenshot comparison failure"
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare " + imageName + " screenshots", e);
        }
    }

    protected void assertDownloadedImageMatches(BufferedImage expected, SelenideElement imageElement, String imageName) {
        assertFalse(
                new ScreenDiffResult(expected, readImage(imageElement, imageName)),
                imageName + " comparison failure"
        );
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
