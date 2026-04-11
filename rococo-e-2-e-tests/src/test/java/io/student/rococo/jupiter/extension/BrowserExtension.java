
package io.student.rococo.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import io.student.rococo.config.Config;
import io.student.rococo.jupiter.annotation.ApiLogin;
import io.student.rococo.page.MainPage;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.ByteArrayInputStream;

import static io.student.rococo.jupiter.extension.ApiLoginExtension.getJsessionIdCookie;
import static io.student.rococo.jupiter.extension.ApiLoginExtension.getToken;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {
    private static final Config CFG = Config.getInstance();

    static {
        String browser = System.getenv().getOrDefault("BROWSER", "chrome");
        Configuration.browser = browser;
        Configuration.timeout = 8000;
        Configuration.pageLoadStrategy = "eager";

        if ("docker".equals(System.getProperty("test.env"))) {
            Configuration.remote = "http://selenoid:4444/wd/hub";

            if ("firefox".equals(browser)) {
                Configuration.browser = "firefox";
                Configuration.browserVersion = "125.0";
                Configuration.browserCapabilities = new FirefoxOptions();
            } else {
                Configuration.browser = "chrome";
                Configuration.browserVersion = "140.0";
                Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
            }
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    String token = getToken();
                    Selenide.open(CFG.frontUrl());
                    Selenide.localStorage().setItem("id_token", token);
                    WebDriverRunner.getWebDriver().manage().addCookie(getJsessionIdCookie());
                    Selenide.open(MainPage.URL, MainPage.class).checkPageLoaded();
                });
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private static void doScreenshot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }
}
