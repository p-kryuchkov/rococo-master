package io.student.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.student.rococo.page.BasePage.CFG;

public class LoginPage {
    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement form = $("form.form");
    private final SelenideElement title = $("h1.form__header");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordButton = $("button.form__password-button");
    private final SelenideElement loginError = $((".form__error.login__error"));
    private final SelenideElement submitButton = $("button.form__submit");
    private final SelenideElement registerLink = $("a[href='/register']");
    private final SelenideElement image = $("img.content__image");

    @Step("Open login page")
    public LoginPage openPage() {
        open(URL);
        return this;
    }

    @Step("Check login page loaded")
    public LoginPage checkPageLoaded() {
        form.shouldBe(visible);
        title.shouldBe(visible).shouldHave(text("Rococo"));
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitButton.shouldBe(visible).shouldHave(text("Войти"));
        registerLink.shouldBe(visible);
        return this;
    }

    @Step("Check login page title")
    public LoginPage checkTitle() {
        title.shouldHave(text("Rococo"));
        return this;
    }

    @Step("Set username: {username}")
    public LoginPage setUsername(String username) {
        usernameInput.shouldBe(visible).setValue(username);
        return this;
    }

    @Step("Set password")
    public LoginPage setPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    @Step("Login with username: {username}")
    public MainPage login(String username, String password) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.shouldBe(visible).setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Step("Submit login form")
    public MainPage submit() {
        submitButton.click();
        return new MainPage();
    }

    @Step("Submit login form")
    public LoginPage submitBadCredentials() {
        submitButton.click();
        return this;
    }

    @Step("Toggle password visibility")
    public LoginPage togglePasswordVisibility() {
        passwordButton.shouldBe(visible).click();
        return this;
    }

    @Step("Check password is hidden")
    public LoginPage checkPasswordHidden() {
        passwordInput.shouldHave(attribute("type", "password"));
        return this;
    }

    @Step("Check password is visible")
    public LoginPage checkPasswordVisible() {
        passwordInput.shouldHave(attribute("type", "text"));
        return this;
    }

    @Step("Open registration page")
    public RegisterPage openRegisterPage() {
        registerLink.click();
        return new RegisterPage();
    }

    @Step("Check login image visible")
    public LoginPage checkImageVisible() {
        image.shouldBe(visible);
        return this;
    }

    @Step("Check login error: {errorText}")
    public LoginPage checkLoginError(String errorText) {
        loginError.shouldBe(visible).shouldHave(text(errorText));
        return this;
    }
}