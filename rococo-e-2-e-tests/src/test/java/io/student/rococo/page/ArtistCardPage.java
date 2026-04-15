package io.student.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.condition.CollectionConditions;
import io.student.rococo.page.component.ArtistUpdateModal;
import io.student.rococo.page.component.PaintingCreateModal;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

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

    @Override
    protected ArtistCardPage self() {
        return this;
    }

    @Step("Open artist card page by id: {artistId}")
    public ArtistCardPage open(String artistId) {
        return openPage(CFG.frontUrl() + "artist/" + artistId);
    }

    @Override
    @Step("Check artist card page is loaded")
    public ArtistCardPage checkPageLoaded() {
        super.checkPageLoaded();
        checkVisible(card, artistName, artistAvatar);
        return this;
    }

    @Step("Check artist page opened for authorized user")
    public ArtistCardPage checkOpenedForAuthorizedUser(String expectedArtistName) {
        return checkPageLoaded()
                .checkArtistNameIsDisplayed(expectedArtistName)
                .checkAddPaintingButtonDisplayed()
                .checkEditArtistButtonIsDisplayed()
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
        checkTitle(artistName, expectedArtistName);
        return this;
    }

    @Step("Check artist biography is displayed")
    public ArtistCardPage checkArtistBiographyIsDisplayed(String expectedBiography) {
        artistBiography.shouldBe(visible).shouldHave(com.codeborne.selenide.Condition.text(expectedBiography));
        return this;
    }

    @Step("Check edit artist button is displayed")
    public ArtistCardPage checkEditArtistButtonIsDisplayed() {
        checkVisible(editArtistButton);
        return this;
    }

    @Step("Check edit artist button is not displayed")
    public ArtistCardPage checkEditArtistButtonIsNotDisplayed() {
        checkDoesNotExist(editArtistButton);
        return this;
    }

    @Step("Open edit artist modal")
    public ArtistUpdateModal openEditArtistForm() {
        editArtistButton.shouldBe(visible).click();
        return new ArtistUpdateModal().checkModalOpened();
    }

    @Step("Edit artist")
    public ArtistCardPage editArtist(String name, String biography, BufferedImage photo) {
        openEditArtistForm().updateArtist(name, biography, photo);
        return checkPageLoaded();
    }

    @Step("Check artist photo screenshot matches expected image")
    public ArtistCardPage assertPhotoScreenshotsMatch(BufferedImage expected) {
        assertScreenshotMatches(expected, artistAvatar, "artist photo");
        return this;
    }

    @Step("Check downloaded artist photo matches expected image")
    public ArtistCardPage assertDownloadedPhotoMatches(BufferedImage expected) {
        assertDownloadedImageMatches(expected, artistPhoto, "artist photo");
        return this;
    }

    @Step("Check add painting button is displayed")
    public ArtistCardPage checkAddPaintingButtonDisplayed() {
        checkVisible(addPaintingButton);
        return this;
    }

    @Step("Check add painting button is not displayed")
    public ArtistCardPage checkAddPaintingButtonNotDisplayed() {
        checkDoesNotExist(addPaintingButton);
        return this;
    }

    @Step("Open create painting modal")
    public PaintingCreateModal openCreatePaintingModal() {
        addPaintingButton.shouldBe(visible).click();
        return new PaintingCreateModal().checkModalLoadedWithoutArtistSelect();
    }

    @Step("Add painting from artist page")
    public ArtistCardPage addPainting(String title,
                                      String museumTitle,
                                      BufferedImage image,
                                      String description) {
        openCreatePaintingModal()
                .createPaintingFromArtistPage(title, museumTitle, image, description)
                .checkModalClosed();

        return checkPageLoaded();
    }

    @Step("Check paintings section is displayed")
    public ArtistCardPage checkPaintingsSectionIsDisplayed() {
        checkVisible(paintingsSection, paintingsList);
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
        checkPaintingsSectionIsDisplayed();
        paintingCards.shouldHave(size(0));
        return this;
    }

    @Step("Check paintings count is {expectedCount}")
    public ArtistCardPage checkPaintingsCount(int expectedCount) {
        checkPaintingsSectionIsDisplayed();
        paintingCards.shouldHave(size(expectedCount));
        return this;
    }

    @Step("Check painting '{title}' is displayed")
    public ArtistCardPage checkPaintingIsDisplayed(String title) {
        cardByText(paintingCards, title);
        return this;
    }

    @Step("Check painting '{title}' is not displayed")
    public ArtistCardPage checkPaintingIsNotDisplayed(String title) {
        paintingCards.findBy(com.codeborne.selenide.Condition.text(title)).shouldNot(com.codeborne.selenide.Condition.exist);
        return this;
    }

    @Step("Check painting image is displayed for '{title}'")
    public ArtistCardPage checkPaintingImageIsDisplayed(String title) {
        cardByText(paintingCards, title).$("img").shouldBe(visible).shouldHave(image);
        return this;
    }

    @Step("Check painting link is displayed for '{title}'")
    public ArtistCardPage checkPaintingLinkIsDisplayed(String title) {
        cardByText(paintingCards, title).$("a").shouldBe(visible);
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
        paintingCards.shouldHave(CollectionConditions.exactTexts(titles));
        return this;
    }
}
