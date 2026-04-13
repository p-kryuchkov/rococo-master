package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.condition.PaintingConditions;
import io.student.rococo.page.component.ArtistUpdateModal;
import io.student.rococo.page.component.PaintingCreateModal;
import io.student.rococo.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ArtistCardPage extends BasePage<ArtistCardPage> {

    private final SelenideElement card = $("article.card");

    private final SelenideElement artistName = card.$(".card-header");
    private final SelenideElement artistAvatar = card.$("[data-testid='avatar']");
    private final SelenideElement artistPhoto = artistAvatar.$("img");
    private final SelenideElement artistBiography = card.$("section p");
    private final SelenideElement editArtistButton = card.$("[data-testid='edit-artist']");
    private final SelenideElement addPaintingButton = pageContent.$(byText("Добавить картину"));

    private final SelenideElement paintingsSection = card.$("section + section");
    private final SelenideElement paintingsList = paintingsSection.$("ul");
    private final ElementsCollection paintingCards = paintingsList.$$(":scope > li");

    private final ArtistUpdateModal artistUpdateModal = new ArtistUpdateModal();

    @Step("Open artist card page by id: {artistId}")
    public ArtistCardPage open(String artistId) {
        Selenide.open(CFG.frontUrl() + "artist/" + artistId);
        return this;
    }

    @Override
    @Step("Check artist card page is loaded")
    public ArtistCardPage checkPageLoaded() {
        super.checkPageLoaded();
        card.shouldBe(visible);
        artistName.shouldBe(visible);
        artistAvatar.shouldBe(visible);
        return this;
    }

    @Step("Check artist page opened for authorized user")
    public ArtistCardPage checkOpenedForAuthorizedUser(String expectedArtistName) {
        return checkPageLoaded()
                .checkArtistNameIsDisplayed(expectedArtistName)
                .checkAddPaintingButtonDisplayed()
                .checkLoginButtonIsNotDisplayed();
    }

    @Step("Check artist page opened for unauthorized user")
    public ArtistCardPage checkOpenedForUnauthorizedUser(String expectedArtistName) {
        return checkPageLoaded()
                .checkArtistNameIsDisplayed(expectedArtistName)
                .checkAddPaintingButtonNotDisplayed()
                .checkEditArtistButtonIsNotDisplayed()
                .checkLoginButtonIsDisplayed();
    }

    @Step("Check artist name '{expectedArtistName}' is displayed")
    public ArtistCardPage checkArtistNameIsDisplayed(String expectedArtistName) {
        artistName.shouldBe(visible).shouldHave(text(expectedArtistName));
        return this;
    }

    @Step("Check artist biography is displayed")
    public ArtistCardPage checkArtistBiographyIsDisplayed(String expectedBiography) {
        artistBiography.shouldBe(visible).shouldHave(text(expectedBiography));
        return this;
    }

    @Step("Check edit artist button is displayed")
    public ArtistCardPage checkEditArtistButtonIsDisplayed() {
        editArtistButton.shouldBe(visible);
        return this;
    }

    @Step("Check edit artist button is not displayed")
    public ArtistCardPage checkEditArtistButtonIsNotDisplayed() {
        editArtistButton.shouldNot(exist);
        return this;
    }

    @Step("Open edit artist modal")
    public ArtistUpdateModal openEditArtistForm() {
        editArtistButton.shouldBe(visible).click();
        artistUpdateModal.checkModalOpened();
        return artistUpdateModal;
    }

    @Step("Edit artist")
    public ArtistCardPage editArtist(String name, String biography, BufferedImage photo) {
        openEditArtistForm().updateArtist(name, biography, photo);
        return checkPageLoaded();
    }

    @Step("Screenshot artist photo")
    public File screenshotArtistPhoto() {
        return artistAvatar.screenshot();
    }

    @Step("Check artist photo screenshot matches expected image")
    public ArtistCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        try {
            assertFalse(
                    new ScreenDiffResult(expected, ImageIO.read(screenshotArtistPhoto())),
                    "Screen comparison failure"
            );
            return this;
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare artist photo screenshots", e);
        }
    }

    @Step("Download artist photo")
    public BufferedImage downloadArtistPhoto() {
        try {
            String src = artistPhoto.shouldBe(visible).getAttribute("src");

            if (src == null || src.isBlank()) {
                throw new IllegalStateException("Artist photo src is empty");
            }

            return readImageBySrc(src);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download artist photo", e);
        }
    }

    @Step("Check downloaded artist photo matches expected image")
    public ArtistCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertFalse(
                new ScreenDiffResult(expected, downloadArtistPhoto()),
                "Screen comparison failure"
        );
        return this;
    }

    @Step("Check add painting button is displayed")
    public ArtistCardPage checkAddPaintingButtonDisplayed() {
        addPaintingButton.shouldBe(visible);
        return this;
    }

    @Step("Check add painting button is not displayed")
    public ArtistCardPage checkAddPaintingButtonNotDisplayed() {
        addPaintingButton.shouldNot(exist);
        return this;
    }

    @Step("Open create painting modal")
    public PaintingCreateModal openCreatePaintingModal() {
        addPaintingButton.shouldBe(visible).click();
        return new PaintingCreateModal();
    }

    @Step("Add painting from artist page")
    public ArtistCardPage addPainting(String title,
                                      String museumTitle,
                                      BufferedImage image,
                                      String description) {
        openCreatePaintingModal()
                .checkModalLoadedWithoutArtistSelect()
                .createPaintingFromArtistPage(title, museumTitle, image, description)
                .checkModalClosed();

        return checkPageLoaded();
    }

    @Step("Check paintings section is displayed")
    public ArtistCardPage checkPaintingsSectionIsDisplayed() {
        paintingsSection.shouldBe(visible);
        paintingsList.shouldBe(visible);
        return this;
    }

    @Step("Check artist has paintings")
    public ArtistCardPage checkArtistHasPaintings() {
        checkPaintingsSectionIsDisplayed();
        paintingCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check artist has no paintings")
    public ArtistCardPage checkArtistHasNoPaintings() {
        paintingCards.shouldHave(size(0));
        return this;
    }

    @Step("Check paintings count is {expectedCount}")
    public ArtistCardPage checkPaintingsCount(int expectedCount) {
        paintingCards.shouldHave(size(expectedCount));
        return this;
    }

    @Step("Check painting '{title}' is displayed")
    public ArtistCardPage checkPaintingIsDisplayed(String title) {
        paintingCard(title).shouldBe(visible);
        return this;
    }

    @Step("Check painting '{title}' is not displayed")
    public ArtistCardPage checkPaintingIsNotDisplayed(String title) {
        paintingCards.findBy(text(title)).shouldNot(exist);
        return this;
    }

    @Step("Check painting image is displayed for '{title}'")
    public ArtistCardPage checkPaintingImageIsDisplayed(String title) {
        paintingCard(title).$("img").shouldBe(visible);
        return this;
    }

    @Step("Check painting link is displayed for '{title}'")
    public ArtistCardPage checkPaintingLinkIsDisplayed(String title) {
        paintingCard(title).$("a").shouldBe(visible);
        return this;
    }

    @Step("Check painting card is displayed: {title}")
    public ArtistCardPage checkPaintingCardDisplayed(String title) {
        return checkArtistHasPaintings()
                .checkPaintingIsDisplayed(title)
                .checkPaintingImageIsDisplayed(title)
                .checkPaintingLinkIsDisplayed(title);
    }

    @Step("Check paintings are displayed")
    public ArtistCardPage checkPaintingsAreDisplayed(String... titles) {
        paintingCards.shouldHave(PaintingConditions.paintings(titles));
        return this;
    }

    private SelenideElement paintingCard(String title) {
        return paintingCards.findBy(text(title)).shouldBe(visible);
    }
}