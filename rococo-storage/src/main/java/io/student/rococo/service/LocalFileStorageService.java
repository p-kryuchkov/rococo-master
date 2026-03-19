package io.student.rococo.service;


import io.student.rococo.config.StorageProperties;
import io.student.rococo.exception.StorageException;
import io.student.rococo.model.Directory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final StorageProperties storageProperties;

    public LocalFileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public String save(byte[] content, Directory directory, String originalFileName) {
        try {
            validateContent(content);

            Path rootPath = Path.of(storageProperties.root());
            Path targetDirectory = rootPath.resolve(directory.folder());
            Files.createDirectories(targetDirectory);

            String extension = getExtension(originalFileName);
            String generatedFileName = UUID.randomUUID() + extension;

            Path targetFile = targetDirectory.resolve(generatedFileName);
            Files.write(targetFile, content);

            return directory + "/" + generatedFileName;
        } catch (IOException e) {
            throw new StorageException("Failed to save file", e);
        }
    }

    @Override
    public void delete(String relativePath) {
        try {
            if (relativePath == null || relativePath.isBlank()) {
                return;
            }

            Path rootPath = Path.of(storageProperties.root());
            Path targetFile = rootPath.resolve(relativePath).normalize();
            Files.deleteIfExists(targetFile);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + relativePath, e);
        }
    }

    private void validateContent(byte[] content) {
        if (content == null || content.length == 0) {
            throw new StorageException("File is empty");
        }
    }

    private String getExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf('.'));
    }
}