package com.jobportal.service;

import com.jobportal.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new BadRequestException("Could not create upload directory: " + this.uploadDir);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or missing");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "resume" : file.getOriginalFilename());
        if (original.contains("..")) {
            throw new BadRequestException("Invalid file name: " + original);
        }

        String extension = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = original.substring(dotIndex);
        }

        String storedName = UUID.randomUUID() + extension;
        Path target = uploadDir.resolve(storedName).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestException("Could not store file: " + file.getOriginalFilename());
        }

        return target.toString();
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            throw new BadRequestException("Could not delete file: " + filePath);
        }
    }
}