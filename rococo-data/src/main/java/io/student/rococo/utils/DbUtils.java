package io.student.rococo.utils;

import io.student.rococo.exception.FieldValidationException;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public class DbUtils {
    @Nonnull
    public static UUID parseUuid(@Nonnull String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new FieldValidationException("Invalid UUID: " + id);
        }
    }
}
