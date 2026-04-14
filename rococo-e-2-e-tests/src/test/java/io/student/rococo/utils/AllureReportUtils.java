package io.student.rococo.utils;

import io.student.rococo.model.allure.AllureResultFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureReportUtils {
    private static final Path ALLURE_RESULTS_PATH = Path.of("rococo-e-2-e-tests", "build", "allure-results");

    public static List<AllureResultFile> readAllureResultFiles() {
        try (Stream<Path> files = Files.list(ALLURE_RESULTS_PATH)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(AllureReportUtils::toAllureResultFile)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read allure results from " + ALLURE_RESULTS_PATH.toAbsolutePath(), e
            );
        }
    }

    private static AllureResultFile toAllureResultFile(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            return new AllureResultFile(
                    file.getFileName().toString(),
                    base64
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.toAbsolutePath(), e);
        }
    }
}
