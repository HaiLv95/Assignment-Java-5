package com.assignmentjava.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileSystemStorageServices {
    String getStoredFileName(MultipartFile file, String id);

    void store(MultipartFile file, String storedFilename) throws Exception;

    Resource loadAsResource(String filename) throws Exception;

    Path load(String filename);

    void delete(String storedFilename) throws IOException;

    void init() throws Exception;
}
