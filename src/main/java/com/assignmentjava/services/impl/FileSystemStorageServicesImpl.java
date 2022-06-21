package com.assignmentjava.services.impl;

import com.assignmentjava.config.StorageProperties;
import com.assignmentjava.services.FileSystemStorageServices;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Service
public class FileSystemStorageServicesImpl implements FileSystemStorageServices {
    private final Path rootLocation;

    @Override
    public String getStoredFileName(MultipartFile file, String id) {
        //file.getOriginalFilename() => lấy tên file
        //FilenameUtils.getExtension(file.getOriginalFilename()) lấy duôi file
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        return "p" + id + "." + ext;
    }
//    AppConfig appConfig
    public FileSystemStorageServicesImpl(StorageProperties appConfig) {
        this.rootLocation = Paths.get(appConfig.getLocation());
    }


    //copy file được chọn từ trong local lưu sang server
    @Override
    public void store(MultipartFile file, String storedFilename) throws Exception {
        try {
            if (file.isEmpty()) {
                throw new Exception("Failed to store empty file");
            }
            Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new Exception("Cannot store file outside current directory");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new Exception("Failed to store file", e);
        }
    }

    //trả về resource của file
    @Override
    public Resource loadAsResource(String fileName) throws Exception {
        try {
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Could not read file: " + fileName);
        }
    }

    //tìm file trong thư mục lưu và trả về đường dẫn
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    //xóa file đã lưu
    @Override
    public void delete(String storedFilename) throws IOException {
        Path destinationFile = rootLocation.resolve(Paths.get(storedFilename));
        Files.deleteIfExists(destinationFile);
    }

    //Khởi tạo thư mục lưu file
    @Override
    public void init() throws Exception {
        try {
            Files.createDirectories(rootLocation);
            System.out.println(rootLocation.toString());
        } catch (Exception e) {
            throw new Exception("Could Not Initialize storage", e);
        }
    }
}
