package io.student.rococo.jupiter.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Museum {
    String description() default "";
    String title() default "Эрмитаж";
    String photo() default "";

    String city() default "Санкт-Петербург";
    String countryName() default "Россия";
}