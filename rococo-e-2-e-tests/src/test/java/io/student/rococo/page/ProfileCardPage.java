package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProfileCardPage extends BasePage<ProfileCardPage> {

    private final SelenideElement modalBackdrop = $("[data-testid='modal-backdrop']");
    private final SelenideElement modal = $("[data-testid='modal-component']");
    private final SelenideElement title = $("header.text-2xl.font-bold");

    private final SelenideElement logoutButton = $("button.variant-ghost");
    private final SelenideElement avatar = $("figure[data-testid='avatar']");
    private final SelenideElement avatarImage = $("figure[data-testid='avatar'] img");
    private final SelenideElement username = $("h4.text-center");

    private final SelenideElement photoInput = $("input[name='content']");
    private final SelenideElement firstNameInput = $("input[name='firstname']");
    private final SelenideElement surnameInput = $("input[name='surname']");

    private final SelenideElement closeButton = $("button.variant-ringed");
    private final SelenideElement updateButton = $("button.variant-filled-primary");

    @Override
    @Step("Check profile modal loaded")
    public ProfileCardPage checkPageLoaded() {
        modalBackdrop.shouldBe(visible);
        modal.shouldBe(visible);
        title.shouldBe(visible).shouldHave(text("Профиль"));
        avatar.shouldBe(visible);
        username.shouldBe(visible);
        firstNameInput.shouldBe(visible);
        surnameInput.shouldBe(visible);
        updateButton.shouldBe(visible).shouldHave(text("Обновить профиль"));
        closeButton.shouldBe(visible).shouldHave(text("Закрыть"));
        logoutButton.shouldBe(visible).shouldHave(text("Выйти"));
        return this;
    }

    @Step("Check profile username: {expectedUsername}")
    public ProfileCardPage checkUsername(String expectedUsername) {
        username.shouldHave(text(expectedUsername));
        return this;
    }

    @Step("Check first name placeholder")
    public ProfileCardPage checkFirstNamePlaceholder() {
        firstNameInput.shouldHave(attribute("placeholder", "Ваше имя..."));
        return this;
    }

    @Step("Check surname placeholder")
    public ProfileCardPage checkSurnamePlaceholder() {
        surnameInput.shouldHave(attribute("placeholder", "Ваша фамилия..."));
        return this;
    }

    @Step("Set first name: {firstName}")
    public ProfileCardPage setFirstName(String firstName) {
        firstNameInput.shouldBe(visible).setValue(firstName);
        return this;
    }

    @Step("Set surname: {surname}")
    public ProfileCardPage setSurname(String surname) {
        surnameInput.shouldBe(visible).setValue(surname);
        return this;
    }

    @Step("Upload profile photo: {filePath}")
    public ProfileCardPage uploadPhoto(String filePath) {
        photoInput.shouldBe(visible).uploadFromClasspath(filePath);
        return this;
    }

    @Step("Click update profile button")
    public ProfileCardPage updateProfile() {
        updateButton.click();
        return this;
    }

    @Step("Update profile")
    public ProfileCardPage updateProfile(String firstName, String surname) {
        firstNameInput.shouldBe(visible).setValue(firstName);
        surnameInput.shouldBe(visible).setValue(surname);
        updateButton.click();
        return this;
    }

    @Step("Close profile modal")
    public MainPage closeProfile() {
        closeButton.click();
        return new MainPage();
    }

    @Step("Logout from profile")
    public MainPage logout() {
        logoutButton.click();
        return new MainPage();
    }

    @Step("Check avatar visible")
    public ProfileCardPage checkAvatarVisible() {
        avatar.shouldBe(visible);
        return this;
    }

    @Step("Check avatar image visible")
    public ProfileCardPage checkAvatarImageVisible() {
        avatarImage.shouldBe(visible);
        return this;
    }
}