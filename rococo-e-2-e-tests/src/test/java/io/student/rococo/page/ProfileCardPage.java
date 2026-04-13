package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
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
    protected ProfileCardPage self() {
        return this;
    }

    @Override
    @Step("Check profile modal loaded")
    public ProfileCardPage checkPageLoaded() {
        checkVisible(modalBackdrop, modal, avatar, username, firstNameInput, surnameInput, photoInput);
        checkTitle(title, "Профиль");
        closeButton.shouldBe(visible).shouldHave(text("Закрыть"));
        updateButton.shouldBe(visible).shouldHave(text("Обновить профиль"));
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

    @Step("Update profile")
    public MainPage updateProfile(String firstName, String surname) {
        return setFirstName(firstName)
                .setSurname(surname)
                .submitUpdate();
    }

    @Step("Submit profile update")
    public MainPage submitUpdate() {
        updateButton.shouldBe(visible).click();
        return new MainPage();
    }

    @Step("Close profile modal")
    public MainPage closeProfile() {
        closeButton.shouldBe(visible).click();
        return new MainPage();
    }

    @Step("Logout from profile")
    public MainPage logout() {
        logoutButton.shouldBe(visible).click();
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

    @Step("Check first name value: {expectedFirstName}")
    public ProfileCardPage checkFirstName(String expectedFirstName) {
        firstNameInput.shouldHave(value(expectedFirstName));
        return this;
    }

    @Step("Check surname value: {expectedSurname}")
    public ProfileCardPage checkSurname(String expectedSurname) {
        surnameInput.shouldHave(value(expectedSurname));
        return this;
    }

    @Step("Check profile data")
    public ProfileCardPage checkProfileData(String firstName, String surname) {
        return checkFirstName(firstName)
                .checkSurname(surname);
    }
}
