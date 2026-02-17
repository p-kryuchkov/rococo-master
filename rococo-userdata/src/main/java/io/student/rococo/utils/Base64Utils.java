package io.student.rococo.utils;

import java.util.Base64;

public class Base64Utils {
    public static byte[] decodeImageFromB64ToBytes(String content){
        if (content.startsWith("data:image")) {
            content = content.substring(content.indexOf(",") + 1);
            return Base64.getDecoder().decode(content);
        }
        else throw new IllegalArgumentException("Base64 string is not a picture");
    }

    public static String encodeImageFromBytesToB64(byte[] imageBytes){
        return new String("data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes));
    }
}
