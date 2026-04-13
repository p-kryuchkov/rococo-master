package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.PaintingPage;
import io.student.rococo.utils.ImageUploadHelper;
import org.openqa.selenium.Keys;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingCreateModal {

    private final SelenideElement modalBackdrop = $("[data-testid='modal-backdrop']");
    private final SelenideElement modal = $("[data-testid='modal-component']");
    private final SelenideElement form = modal.$("form.modal-form");
    private final SelenideElement paintingTitleInput = form.$("input[name='title']");
    private final SelenideElement imageInput = form.$("input[name='content']");
    private final SelenideElement artistSelect = form.$("select[name='authorId']");
    private final SelenideElement descriptionTextarea = form.$("textarea[name='description']");
    private final SelenideElement museumSelect = form.$("select[name='museumId']");
    private final SelenideElement closeButton = form.$("button[type='button']");
    private final SelenideElement addButton = form.$("button[type='submit']");

    @Step("Check painting create modal loaded with artist select")
    public PaintingCreateModal checkModalLoadedWithArtistSelect() {
        checkCommonState();
        artistSelect.shouldBe(visible);
        return this;
    }

    @Step("Check painting create modal loaded without artist select")
    public PaintingCreateModal checkModalLoadedWithoutArtistSelect() {
        checkCommonState();
        artistSelect.shouldNot(exist);
        return this;
    }

    @Step("Create painting: {paintingTitle}")
    public PaintingCreateModal createPainting(String paintingTitle,
                                              String authorName,
                                              String museumName,
                                              BufferedImage image,
                                              String description) {
        return setPaintingTitle(paintingTitle)
                .uploadImage(image)
                .selectArtist(authorName)
                .setDescription(description)
                .selectMuseum(museumName)
                .submit();
    }

    @Step("Create painting from artist page: {paintingTitle}")
    public PaintingCreateModal createPaintingFromArtistPage(String paintingTitle,
                                                            String museumName,
                                                            BufferedImage image,
                                                            String description) {
        return setPaintingTitle(paintingTitle)
                .uploadImage(image)
                .setDescription(description)
                .selectMuseum(museumName)
                .submit();
    }

    @Step("Set painting title: {paintingTitle}")
    public PaintingCreateModal setPaintingTitle(String paintingTitle) {
        paintingTitleInput.shouldBe(visible).clear();
        paintingTitleInput.setValue(paintingTitle);
        return this;
    }

    @Step("Upload painting image")
    public PaintingCreateModal uploadImage(BufferedImage image) {
        ImageUploadHelper.uploadPng(imageInput.should(exist), image, "painting-create-");
        return this;
    }

    @Step("Select author: {authorName}")
    public PaintingCreateModal selectArtist(String authorName) {
        artistSelect.shouldBe(visible);

        for (int i = 0; i < 10; i++) {
            scrollArtistSelectDown();
        }

        artistSelect.selectOptionContainingText(authorName);
        return this;
    }

    @Step("Set painting description")
    public PaintingCreateModal setDescription(String description) {
        descriptionTextarea.shouldBe(visible).clear();
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("Select museum: {museumName}")
    public PaintingCreateModal selectMuseum(String museumName) {
        museumSelect.shouldBe(visible);

        for (int i = 0; i < 10; i++) {
            scrollMuseumSelectDown();
        }

        museumSelect.selectOptionContainingText(museumName);
        return this;
    }

    @Step("Submit painting create form")
    public PaintingCreateModal submit() {
        addButton.shouldBe(visible, enabled).click();
        return this;
    }

    @Step("Close painting create modal")
    public PaintingCreateModal close() {
        closeButton.shouldBe(visible).click();
        return this;
    }

    @Step("Check painting create modal closed")
    public PaintingPage checkModalClosed() {
        modalBackdrop.should(disappear);
        modal.should(disappear);
        return new PaintingPage();
    }

    @Step("Scroll museum select down")
    public PaintingCreateModal scrollMuseumSelectDown() {
        museumSelect.shouldBe(visible).click();
        museumSelect.selectOption(1);
        museumSelect.sendKeys(Keys.END);
        return this;
    }

    @Step("Scroll artist select down")
    public PaintingCreateModal scrollArtistSelectDown() {
        artistSelect.shouldBe(visible).click();
        museumSelect.selectOption(1);
        museumSelect.sendKeys(Keys.END);
        return this;
    }

    private void checkCommonState() {
        modalBackdrop.shouldBe(visible);
        modal.shouldBe(visible);
        form.shouldBe(visible);
        paintingTitleInput.shouldBe(visible);
        imageInput.should(exist);
        descriptionTextarea.shouldBe(visible);
        museumSelect.shouldBe(visible);
        closeButton.shouldBe(visible).shouldHave(text("Закрыть"));
        addButton.shouldBe(visible).shouldHave(text("Добавить"));
    }
}
