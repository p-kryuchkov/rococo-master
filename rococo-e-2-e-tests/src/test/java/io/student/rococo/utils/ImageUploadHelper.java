package io.student.rococo.utils;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class ImageUploadHelper {

    private ImageUploadHelper() {
    }

    public static void uploadPng(SelenideElement input, BufferedImage image, String prefix) {
        if (image == null) {
            return;
        }
        input.uploadFile(toTempPng(image, prefix));
    }

    public static File toTempPng(@Nonnull BufferedImage image, String prefix) {
        try {
            File tempFile = Files.createTempFile(prefix, ".png").toFile();
            ImageIO.write(image, "png", tempFile);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare image for upload", e);
        }
    }
}
