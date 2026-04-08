package io.student.rococo.jupiter.annotation.meta;

import io.qameta.allure.junit5.AllureJunit5;
import io.student.rococo.data.entity.data.MuseumEntity;
import io.student.rococo.jupiter.extension.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
        AllureJunit5.class,
        ApiLoginExtension.class,
        UserExtension.class, //ToDo Добавь экстеншны на артистов, музеев, картины
        ArtistExtension.class,
        MuseumExtension.class,
        PaintingExtension.class
})
public @interface RestTest {
}