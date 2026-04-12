package io.student.rococo.jupiter.annotation;

import io.student.rococo.jupiter.extension.ScreenshotTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ScreenshotTestExtension.class)
@Test
public @interface ScreenshotTest {
    String value();
    boolean rewriteExpected() default false;
}
