package io.student.rococo.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class Base64UtilsTest {

    @Test
    void decodeImageFromB64ToBytes() {
        final byte[] imageBytes = "test-image".getBytes(StandardCharsets.UTF_8);
        final String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        byte[] result = Base64Utils.decodeImageFromB64ToBytes(base64);

        assertArrayEquals(imageBytes, result);
    }

    @Test
    void decodeImageFromB64ToBytesShouldThrowExceptionWhenStringIsNotImage() {
        final String base64 = Base64.getEncoder().encodeToString("test-image".getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Base64Utils.decodeImageFromB64ToBytes(base64)
        );

        assertEquals("Base64 string is not a picture", exception.getMessage());
    }

    @Test
    void encodeImageFromBytesToB64() {
        final byte[] imageBytes = "test-image".getBytes(StandardCharsets.UTF_8);

        String result = Base64Utils.encodeImageFromBytesToB64(imageBytes);

        assertEquals(
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes),
                result
        );
    }

    @Test
    void encodeAndDecodeImage() {
        final byte[] imageBytes = "test-image".getBytes(StandardCharsets.UTF_8);

        String encoded = Base64Utils.encodeImageFromBytesToB64(imageBytes);
        byte[] decoded = Base64Utils.decodeImageFromB64ToBytes(encoded);

        assertArrayEquals(imageBytes, decoded);
    }
}