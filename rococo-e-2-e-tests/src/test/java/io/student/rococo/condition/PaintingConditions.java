package io.student.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.lang.System.lineSeparator;

@Nonnull
public class PaintingConditions {

    public static WebElementsCondition paintings(String... expectedTitles) {
        if (expectedTitles == null || expectedTitles.length == 0) {
            throw new IllegalArgumentException("No expected painting titles given");
        }

        final String expectedTitlesString = Arrays.toString(expectedTitles);

        return new WebElementsCondition() {
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                List<String> actualTitles = elements.stream()
                        .map(WebElement::getText)
                        .toList();

                for (String expectedTitle : expectedTitles) {
                    WebElement matchedCard = elements.stream()
                            .filter(element -> element.getText().contains(expectedTitle))
                            .findFirst()
                            .orElse(null);

                    if (matchedCard == null) {
                        return rejected(
                                String.format(
                                        "Painting '%s' was not found. Actual paintings: %s",
                                        expectedTitle,
                                        actualTitles
                                ),
                                actualTitles
                        );
                    }

                    CheckResult result = checkPaintingCard(expectedTitle, matchedCard);
                    if (result.verdict() == CheckResult.Verdict.REJECT) {
                        return result;
                    }
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expectedTitlesString;
            }

            @Override
            public void fail(CollectionSource collection,
                             CheckResult lastCheckResult,
                             @Nullable Exception cause,
                             long timeoutMs) {
                throw new UIAssertionError(
                        lastCheckResult.message()
                                + lineSeparator() + "Actual: " + lastCheckResult.getActualValue()
                                + lineSeparator() + "Expected: " + expectedValue()
                                + lineSeparator() + "Collection: " + collection.description(),
                        toString(),
                        lastCheckResult.getActualValue()
                );
            }
        };
    }

    private static CheckResult checkPaintingCard(String expectedTitle, WebElement actualCard) {
        String actualText = actualCard.getText();

        if (!actualText.contains(expectedTitle)) {
            return rejected(
                    String.format(
                            "Painting title mismatch (expected: %s, actual text: %s)",
                            expectedTitle,
                            actualText
                    ),
                    actualText
            );
        }

        List<WebElement> images = actualCard.findElements(By.cssSelector("img"));
        if (images.isEmpty()) {
            return rejected(
                    String.format("Painting '%s' has no image", expectedTitle),
                    actualText
            );
        }

        String imageSrc = images.get(0).getAttribute("src");
        if (imageSrc == null || imageSrc.isBlank()) {
            return rejected(
                    String.format("Painting '%s' image src is empty", expectedTitle),
                    imageSrc
            );
        }

        List<WebElement> links = actualCard.findElements(By.cssSelector("a"));
        if (links.isEmpty()) {
            return rejected(
                    String.format("Painting '%s' has no link", expectedTitle),
                    actualText
            );
        }

        String href = links.get(0).getAttribute("href");
        if (href == null || href.isBlank() || !href.contains("/painting/")) {
            return rejected(
                    String.format(
                            "Painting '%s' link mismatch (actual href: %s)",
                            expectedTitle,
                            href
                    ),
                    href
            );
        }

        return accepted();
    }
}