package io.student.rococo.utils;

import jakarta.annotation.Nonnull;

import java.util.Base64;

public class Base64Utils {
    @Nonnull
    public static byte[] decodeImageFromB64ToBytes(@Nonnull String content){
        if (content.startsWith("data:image")) {
            content = content.substring(content.indexOf(",") + 1);
            return Base64.getDecoder().decode(content);
        }
        else throw new IllegalArgumentException("Base64 string is not a picture");
    }

    @Nonnull
    public static String encodeImageFromBytesToB64(@Nonnull byte[] imageBytes){
        return new String("data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes));
    }
}
