package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.PaintingCardPage;
import io.student.rococo.utils.ImageUploadHelper;
import org.openqa.selenium.Keys;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingUpdateModal {

    private final SelenideElement modalBackdrop = $("[data-testid='modal-backdrop']");
    private final SelenideElement modal = $("[data-testid='modal-component']");
    private final SelenideElement title = modal.$("header");
    private final SelenideElement form = modal.$("form.modal-form");
    private final SelenideElement previewImage = form.$("img");
    private final SelenideElement imageInput = form.$("input[name='content']");
    private final SelenideElement paintingTitleInput = form.$("input[name='title']");
    private final SelenideElement artistSelect = form.$("select[name='authorId']");
    private final SelenideElement descriptionTextarea = form.$("textarea[name='description']");
    private final SelenideElement museumSelect = form.$("select[name='museumId']");
    private final SelenideElement closeButton = form.$("button[type='button']");
    private final SelenideElement saveButton = form.$("button[type='submit']");

    @Step("Check painting update modal loaded")
    public PaintingUpdateModal checkModalLoaded() {
        modalBackdrop.shouldBe(visible);
        modal.shouldBe(visible);
        title.shouldBe(visible).shouldHave(text("Редактировать картину"));
        form.shouldBe(visible);
        previewImage.shouldBe(visible);
        imageInput.should(exist);
        paintingTitleInput.shouldBe(visible);
        artistSelect.shouldBe(visible);
        descriptionTextarea.shouldBe(visible);
        museumSelect.shouldBe(visible);
        closeButton.shouldBe(visible).shouldHave(text("Закрыть"));
        saveButton.shouldBe(visible).shouldHave(text("Сохранить"));
        return this;
    }

    @Step("Close painting update modal")
    public PaintingUpdateModal close() {
        closeButton.shouldBe(visible).click();
        return this;
    }

    @Step("Check painting update modal closed")
    public PaintingCardPage checkModalClosed() {
        modalBackdrop.should(disappear);
        modal.should(disappear);
        return new PaintingCardPage();
    }

    @Step("Edit painting")
    public PaintingCardPage editPainting(String paintingTitle,
                                         String authorName,
                                         String museumName,
                                         String description,
                                         BufferedImage image) {
        setPaintingTitle(paintingTitle);
        selectArtist(authorName);
        selectMuseum(museumName);
        setDescription(description);
        uploadImage(image);
        save();
        modalBackdrop.should(disappear);
        return new PaintingCardPage();
    }

    @Step("Set painting title: {paintingTitle}")
    public PaintingUpdateModal setPaintingTitle(String paintingTitle) {
        paintingTitleInput.shouldBe(visible).clear();
        paintingTitleInput.setValue(paintingTitle);
        return this;
    }

    @Step("Select author: {authorName}")
    public PaintingUpdateModal selectArtist(String authorName) {
        artistSelect.shouldBe(visible);

        for (int i = 0; i < 10; i++) {
            scrollArtistSelectDown();
        }

        artistSelect.selectOptionContainingText(authorName);
        return this;
    }

    @Step("Set painting description")
    public PaintingUpdateModal setDescription(String description) {
        descriptionTextarea.shouldBe(visible).clear();
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("Select museum: {museumName}")
    public PaintingUpdateModal selectMuseum(String museumName) {
        museumSelect.shouldBe(visible);

        for (int i = 0; i < 10; i++) {
            scrollMuseumSelectDown();
        }

        museumSelect.selectOptionContainingText(museumName);
        return this;
    }

    @Step("Upload painting image")
    public PaintingUpdateModal uploadImage(BufferedImage image) {
        ImageUploadHelper.uploadPng(imageInput.should(exist), image, "painting-update-");
        return this;
    }

    @Step("Scroll museum select down")
    public PaintingUpdateModal scrollMuseumSelectDown() {
        museumSelect.shouldBe(visible).click();
        museumSelect.selectOption(1);
        museumSelect.sendKeys(Keys.END);
        return this;
    }

    @Step("Scroll artist select down")
    public PaintingUpdateModal scrollArtistSelectDown() {
        artistSelect.shouldBe(visible).click();
        museumSelect.selectOption(1);
        museumSelect.sendKeys(Keys.END);
        return this;
    }

    @Step("Save painting changes")
    public PaintingUpdateModal save() {
        saveButton.shouldBe(visible, enabled).click();
        return this;
    }
}
