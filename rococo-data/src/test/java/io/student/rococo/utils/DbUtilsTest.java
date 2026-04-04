package io.student.rococo.utils;

import io.student.rococo.exception.FieldValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DbUtilsTest {

    @Test
    void parseValidUuid() {
        final String id = "123e4567-e89b-12d3-a456-426614174000";

        final UUID result = DbUtils.parseUuid(id);

        assertEquals(UUID.fromString(id), result);
    }

    @Test
    void parseInvalidUuid() {
        final String id = "invalid-uuid";

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> DbUtils.parseUuid(id)
        );

        assertEquals("Invalid UUID: " + id, exception.getMessage());
    }

    @Test
    void parseNullUuid() {
        final String id = null;

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> DbUtils.parseUuid(id)
        );

        assertEquals("Invalid UUID: null", exception.getMessage());
    }
}