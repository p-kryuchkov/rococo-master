package io.student.rococo.utils;

import io.student.rococo.exception.FieldValidationException;

import java.util.UUID;

public class DbUtils {
    public static UUID parseUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new FieldValidationException("Invalid UUID: " + id);
        }
    }
}
