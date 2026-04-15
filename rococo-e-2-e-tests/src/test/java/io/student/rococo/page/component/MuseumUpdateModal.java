package io.student.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rococo.utils.ImageUploadHelper;

import java.awt.image.BufferedImage;
import java.io.File;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MuseumUpdateModal {

    private final SelenideElement museumTitleInput = $("input[name='title']");
    private final SelenideElement museumCountrySelect = $("select[name='countryId']");
    private final SelenideElement museumCityInput = $("input[name='city']");
    private final SelenideElement museumPhotoInput = $("input[name='photo']");
    private final SelenideElement museumDescriptionInput = $("textarea[name='description']");
    private final SelenideElement saveMuseumButton = $("button[type='submit']");

    @Step("Check museum update modal is displayed")
    public MuseumUpdateModal checkModalOpened() {
        museumTitleInput.shouldBe(visible);
        museumCountrySelect.shouldBe(visible);
        museumCityInput.shouldBe(visible);
        museumDescriptionInput.shouldBe(visible);
        saveMuseumButton.shouldBe(visible);
        return this;
    }

    @Step("Set museum title: {title}")
    public MuseumUpdateModal setTitle(String title) {
        museumTitleInput.shouldBe(visible).clear();
        museumTitleInput.setValue(title);
        return this;
    }

    @Step("Select museum country: {country}")
    public MuseumUpdateModal selectCountry(String country) {
        museumCountrySelect.shouldBe(visible).selectOption(country);
        return this;
    }

    @Step("Set museum city: {city}")
    public MuseumUpdateModal setCity(String city) {
        museumCityInput.shouldBe(visible).clear();
        museumCityInput.setValue(city);
        return this;
    }

    @Step("Set museum description")
    public MuseumUpdateModal setDescription(String description) {
        museumDescriptionInput.shouldBe(visible).clear();
        museumDescriptionInput.setValue(description);
        return this;
    }

    @Step("Upload museum picture")
    public MuseumUpdateModal uploadPicture(BufferedImage image) {
        ImageUploadHelper.uploadPng(museumPhotoInput.shouldBe(visible), image, "museum-update-");
        return this;
    }

    @Step("Upload museum picture: {imageFile}")
    public MuseumUpdateModal uploadPicture(File imageFile) {
        museumPhotoInput.shouldBe(visible).uploadFile(imageFile);
        return this;
    }

    @Step("Save museum changes")
    public MuseumUpdateModal save() {
        saveMuseumButton.shouldBe(visible).click();
        return this;
    }

    @Step("Update museum")
    public MuseumUpdateModal updateMuseum(String title, String country, String city, String description, BufferedImage photo) {
        return setTitle(title)
                .selectCountry(country)
                .setCity(city)
                .setDescription(description)
                .uploadPicture(photo)
                .save();
    }
}
