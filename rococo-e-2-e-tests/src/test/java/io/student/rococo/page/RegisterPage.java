package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.student.rococo.page.BasePage.CFG;

public class RegisterPage {
    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement form = $("#register-form");
    private final SelenideElement title = $("h1.form__header");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitButton = $("button.form__submit");
    private final SelenideElement loginLink = $("a.form__link");
    private final SelenideElement image = $("img.content__image");

    private final SelenideElement usernameError = $((".form__error.error__username"));
    private final SelenideElement passwordError = $(".form__error.error__password");

    private final SelenideElement successRegisterPageForm = $("div.form");
    private final SelenideElement subheader = $("p.form__subheader");
    private final SelenideElement loginButton = $("a.form__submit");

    @Step("Open register page")
    public RegisterPage openPage() {
        open(URL);
        return this;
    }

    @Step("Check register page loaded")
    public RegisterPage checkPageLoaded() {
        form.shouldBe(visible);
        title.shouldBe(visible).shouldHave(text("Rococo"));
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordSubmitInput.shouldBe(visible);
        submitButton.shouldBe(visible).shouldHave(text("Зарегистрироваться"));
        loginLink.shouldBe(visible);
        image.shouldBe(visible);
        return this;
    }

    @Step("Set username: {username}")
    public RegisterPage setUsername(String username) {
        usernameInput.shouldBe(visible).setValue(username);
        return this;
    }

    @Step("Set password")
    public RegisterPage setPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    @Step("Set password submit")
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.shouldBe(visible).setValue(passwordSubmit);
        return this;
    }

    @Step("Register with username: {username}")
    public RegisterPage register(String username, String password, String passwordSubmit) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.shouldBe(visible).setValue(password);
        passwordSubmitInput.shouldBe(visible).setValue(passwordSubmit);
        submitButton.click();
        return this;
    }

    @Step("Submit register form")
    public RegisterPage submit() {
        submitButton.click();
        return this;
    }

    @Step("Open login page")
    public LoginPage openLoginPage() {
        loginLink.click();
        return new LoginPage();
    }

    @Step("Check username placeholder")
    public RegisterPage checkUsernamePlaceholder() {
        usernameInput.shouldHave(attribute("placeholder", "Введите имя пользователя..."));
        return this;
    }

    @Step("Check password placeholder")
    public RegisterPage checkPasswordPlaceholder() {
        passwordInput.shouldHave(attribute("placeholder", "Введите пароль..."));
        return this;
    }

    @Step("Check password submit placeholder")
    public RegisterPage checkPasswordSubmitPlaceholder() {
        passwordSubmitInput.shouldHave(attribute("placeholder", "Повторите пароль..."));
        return this;
    }

    @Step("Check username error: {errorText}")
    public RegisterPage checkUsernameError(String errorText) {
        usernameError.shouldBe(visible).shouldHave(text(errorText));
        return this;
    }

    @Step("Check password error: {errorText}")
    public RegisterPage checkPasswordError(String errorText) {
        passwordError.shouldBe(visible).shouldHave(text(errorText));
        return this;
    }

    @Step("Check success registration page loaded")
    public RegisterPage checkSuccessRegisterPageLoaded() {
        successRegisterPageForm.shouldBe(visible);
        subheader.shouldBe(visible).shouldHave(text("Добро пожаловать в Rococo"));
        loginButton.shouldBe(visible).shouldHave(text("Войти в систему"));
        image.shouldBe(visible);
        return this;
    }

    @Step("Check welcome text")
    public RegisterPage checkWelcomeText() {
        subheader.shouldHave(text("Добро пожаловать в Rococo"));
        return this;
    }

    @Step("Check login button text")
    public RegisterPage checkLoginButtonText() {
        loginButton.shouldHave(text("Войти в систему"));
        return this;
    }

    @Step("Click login button from success registration")
    public MainPage loginAfterRegistration() {
        loginButton.click();
        return new MainPage();
    }
}