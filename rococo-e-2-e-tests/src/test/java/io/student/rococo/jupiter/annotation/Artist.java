package io.student.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Artist {
    String name() default "Шишкин";
    String biography() default "В 1832 году (по другим сведениям, в 1831‑м или 1835‑м) в купеческой семье в Елабуге появился на свет младший сын Иван";
    String photo() default "";
}
