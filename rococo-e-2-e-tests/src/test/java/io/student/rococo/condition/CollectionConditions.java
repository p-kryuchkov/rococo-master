package io.student.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public final class CollectionConditions {

    private CollectionConditions() {
    }

    public static WebElementsCondition exactTexts(String... expectedTexts) {
        List<String> expected = Arrays.stream(expectedTexts)
                .map(String::trim)
                .sorted()
                .toList();

        return new WebElementsCondition() {

            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                List<String> actual = elements.stream()
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(text -> !text.isBlank())
                        .sorted()
                        .toList();

                boolean matches = actual.size() == expected.size()
                        && actual.containsAll(expected);

                return matches
                        ? accepted()
                        : rejected("Expected texts %s in any order but was %s".formatted(expected, actual), actual);
            }

            @Override
            public String toString() {
                return "exact texts in any order " + expected;
            }
        };
    }
}