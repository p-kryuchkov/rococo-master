package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    @Step("Save artist changes")
    public void save() {
        saveArtistButton.shouldBe(visible).click();
    }

    @Step("Update artist with name '{name}'")
    public void updateArtist(String name, String biography, BufferedImage photo) {
        setName(name);
        setBiography(biography);
        if(photo!=null) uploadPicture(photo);
        save();
    }

    @Step("Upload artist picture: {imageFile}")
    public ArtistUpdateModal uploadPicture(File imageFile) {
        artistPhotoInput.uploadFile(imageFile);
        return this;
    }

    @Step("Upload artist picture: {imageFile}")
    public ArtistUpdateModal uploadPicture(BufferedImage imageFile) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("img-", ".png");
            ImageIO.write(imageFile, "png", tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        uploadPicture(tempFile);
        return this;
    }
}