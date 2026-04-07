package io.student.rococo.jupiter.annotation;

import java.util.UUID;

public @interface User {
    String username() default "";
    String firstname() default "";
    String lastname() default "";
    String avatar() default "";
}
