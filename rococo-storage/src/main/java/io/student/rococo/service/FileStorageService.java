package io.student.rococo.service;

import io.student.rococo.model.Directory;

public interface FileStorageService {

    String save(byte[] content, Directory directory, String originalFileName);

    void delete(String relativePath);
}
