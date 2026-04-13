package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.utils.ImageUploadHelper;

import java.awt.image.BufferedImage;
import java.io.File;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class ArtistCreateModal {

    private final SelenideElement modal = $$("header")
            .findBy(text("Новый художник"))
            .closest("div");

    private final SelenideElement title = modal.$("header");
    private final SelenideElement description = modal.$("article");
    private final SelenideElement nameInput = modal.$("input[name='name']");
    private final SelenideElement photoInput = modal.$("input[name='photo']");
    private final SelenideElement biographyInput = modal.$("textarea[name='biography']");
    private final SelenideElement closeButton = modal.$("button[type='button']");
    private final SelenideElement submitButton = modal.$("button[type='submit']");

    @Step("Check create artist modal is displayed")
    public ArtistCreateModal checkModalLoaded() {
        modal.shouldBe(visible);
        title.shouldBe(visible).shouldHave(text("Новый художник"));
        description.shouldBe(visible).shouldHave(text("Заполните форму, чтобы добавить нового художника"));
        nameInput.shouldBe(visible);
        photoInput.shouldBe(visible);
        biographyInput.shouldBe(visible);
        closeButton.shouldBe(visible).shouldHave(text("Закрыть"));
        submitButton.shouldBe(visible).shouldHave(text("Добавить"));
        return this;
    }

    @Step("Set artist name: {name}")
    public ArtistCreateModal setName(String name) {
        nameInput.shouldBe(visible).setValue(name);
        return this;
    }

    @Step("Set artist biography")
    public ArtistCreateModal setBiography(String biography) {
        biographyInput.shouldBe(visible).setValue(biography);
        return this;
    }

    @Step("Upload artist picture")
    public ArtistCreateModal uploadPicture(BufferedImage image) {
        ImageUploadHelper.uploadPng(photoInput.shouldBe(visible), image, "artist-create-");
        return this;
    }

    @Step("Upload artist picture: {imageFile}")
    public ArtistCreateModal uploadPicture(File imageFile) {
        photoInput.shouldBe(visible).uploadFile(imageFile);
        return this;
    }

    @Step("Submit create artist form")
    public ArtistCreateModal submit() {
        submitButton.shouldBe(visible, enabled).click();
        return this;
    }

    @Step("Close create artist modal")
    public ArtistCreateModal close() {
        closeButton.shouldBe(visible).click();
        return this;
    }

    @Step("Check create artist modal is closed")
    public ArtistCreateModal checkModalClosed() {
        modal.should(disappear);
        return this;
    }

    @Step("Create artist")
    public ArtistCreateModal createArtist(String artistName, BufferedImage photo, String biography) {
        setName(artistName);
        uploadPicture(photo);
        setBiography(biography);
        return submit();
    }
}
