package io.student.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Painting {
    String description() default "";

    String title() default "";

    String content() default "";

    Artist artist() default @Artist(name = "Илья Репин");

    Museum museum() default @Museum(title = "Государственный Эрмитаж");
}