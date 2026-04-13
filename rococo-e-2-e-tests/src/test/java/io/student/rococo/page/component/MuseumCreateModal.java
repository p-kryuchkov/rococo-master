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
import static com.codeborne.selenide.Selenide.$;

public class MuseumCreateModal {

    private final SelenideElement modal = $("[data-testid='modal-component']");
    private final SelenideElement form = modal.$("form");
    private final SelenideElement titleInput = form.$("input[name='title']");
    private final SelenideElement countrySelect = form.$("select[name='countryId']");
    private final SelenideElement cityInput = form.$("input[name='city']");
    private final SelenideElement photoInput = form.$("input[name='photo']");
    private final SelenideElement descriptionInput = form.$("textarea[name='description']");
    private final SelenideElement closeButton = form.$("button[type='button']");
    private final SelenideElement submitButton = form.$("button[type='submit']");

    @Step("Check create museum modal is displayed")
    public MuseumCreateModal checkModalLoaded() {
        modal.shouldBe(visible);
        titleInput.shouldBe(visible);
        countrySelect.shouldBe(visible);
        cityInput.shouldBe(visible);
        photoInput.shouldBe(visible);
        descriptionInput.shouldBe(visible);
        closeButton.shouldBe(visible).shouldHave(text("Закрыть"));
        submitButton.shouldBe(visible).shouldHave(text("Добавить"));
        return this;
    }

    @Step("Set museum title: {title}")
    public MuseumCreateModal setTitle(String title) {
        titleInput.shouldBe(visible).setValue(title);
        return this;
    }

    @Step("Select museum country: {country}")
    public MuseumCreateModal selectCountry(String country) {
        countrySelect.shouldBe(visible).selectOption(country);
        return this;
    }

    @Step("Set museum city: {city}")
    public MuseumCreateModal setCity(String city) {
        cityInput.shouldBe(visible).setValue(city);
        return this;
    }

    @Step("Set museum description")
    public MuseumCreateModal setDescription(String description) {
        descriptionInput.shouldBe(visible).setValue(description);
        return this;
    }

    @Step("Upload museum picture")
    public MuseumCreateModal uploadPicture(BufferedImage image) {
        ImageUploadHelper.uploadPng(photoInput.shouldBe(visible), image, "museum-create-");
        return this;
    }

    @Step("Upload museum picture: {imageFile}")
    public MuseumCreateModal uploadPicture(File imageFile) {
        photoInput.shouldBe(visible).uploadFile(imageFile);
        return this;
    }

    @Step("Submit create museum form")
    public MuseumCreateModal submit() {
        submitButton.shouldBe(visible, enabled).click();
        return this;
    }

    @Step("Close create museum modal")
    public MuseumCreateModal close() {
        closeButton.shouldBe(visible).click();
        return this;
    }

    @Step("Check create museum modal is closed")
    public MuseumCreateModal checkModalClosed() {
        modal.should(disappear);
        return this;
    }

    @Step("Create museum")
    public MuseumCreateModal createMuseum(String title, String country, String city, BufferedImage photo, String description) {
        return setTitle(title)
                .selectCountry(country)
                .setCity(city)
                .uploadPicture(photo)
                .setDescription(description)
                .submit();
    }
}
