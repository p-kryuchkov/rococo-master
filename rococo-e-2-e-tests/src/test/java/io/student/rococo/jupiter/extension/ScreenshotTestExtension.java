package io.student.rococo.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.student.rococo.jupiter.annotation.ScreenshotTest;
import io.student.rococo.model.allure.ScreenDiff;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ScreenshotTestExtension implements ParameterResolver, TestExecutionExceptionHandler, AfterEachCallback {
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenshotTestExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenshotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return ImageIO.read(new ClassPathResource(
                extensionContext.getRequiredTestMethod()
                        .getAnnotation(ScreenshotTest.class)
                        .value()
        ).getInputStream());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (context.getRequiredTestMethod().getAnnotation(ScreenshotTest.class).rewriteExpected()
                && getActual() != null) {
            try {
                Path expected = Paths.get(
                        "src/test/resources",
                        context.getRequiredTestMethod()
                                .getAnnotation(ScreenshotTest.class)
                                .value()
                );
                ImageIO.write(getActual(), "png", expected.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Cannot rewrite expected image", e);
            }
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (throwable.getMessage() != null) {
            if (throwable.getMessage().contains("Screen comparison failure")) {
                ScreenDiff screenDiff = new ScreenDiff(
                        "data:image/png;base64,"
                                + Base64.getEncoder().encodeToString(imageToBytes(getExpected())),
                        "data:image/png;base64,"
                                + Base64.getEncoder().encodeToString(imageToBytes(getActual())),
                        "data:image/png;base64,"
                                + Base64.getEncoder().encodeToString(imageToBytes(getDiff())));

                Allure.addAttachment("Screenshot diff",
                        "application/vnd.allure.image.diff",
                        objectMapper.writeValueAsString(screenDiff));
            }
        }
        throw throwable;
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    private static byte[] imageToBytes(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}