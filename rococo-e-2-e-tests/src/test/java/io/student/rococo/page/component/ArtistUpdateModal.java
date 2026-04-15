package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.page.ArtistCardPage;
import io.student.rococo.utils.ImageUploadHelper;

import java.awt.image.BufferedImage;
import java.io.File;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ArtistUpdateModal {

    private final SelenideElement artistNameInput = $("input[name='name']");
    private final SelenideElement artistBiographyInput = $("textarea[name='biography']");
    private final SelenideElement artistPhotoInput = $("input[type='file']");
    private final SelenideElement saveArtistButton = $("button[type='submit']");

    @Step("Check artist update modal is displayed")
    public ArtistUpdateModal checkModalOpened() {
        artistNameInput.shouldBe(visible);
        artistBiographyInput.shouldBe(visible);
        saveArtistButton.shouldBe(visible);
        return this;
    }

    @Step("Set artist name: {name}")
    public ArtistUpdateModal setName(String name) {
        artistNameInput.shouldBe(visible).clear();
        artistNameInput.setValue(name);
        return this;
    }

    @Step("Set artist biography")
    public ArtistUpdateModal setBiography(String biography) {
        artistBiographyInput.shouldBe(visible).clear();
        artistBiographyInput.setValue(biography);
        return this;
    }

    @Step("Upload artist picture")
    public ArtistUpdateModal uploadPicture(BufferedImage image) {
        ImageUploadHelper.uploadPng(artistPhotoInput.shouldBe(visible), image, "artist-update-");
        return this;
    }

    @Step("Upload artist picture: {imageFile}")
    public ArtistUpdateModal uploadPicture(File imageFile) {
        artistPhotoInput.shouldBe(visible).uploadFile(imageFile);
        return this;
    }

    @Step("Save artist changes")
    public ArtistUpdateModal save() {
        saveArtistButton.shouldBe(visible).click();
        return this;
    }

    @Step("Update artist with name '{name}'")
    public ArtistCardPage updateArtist(String name, String biography, BufferedImage photo) {
        setName(name);
        setBiography(biography);
        uploadPicture(photo);
        save();
        return new ArtistCardPage();
    }
}
