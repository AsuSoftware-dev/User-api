package com.asusoftware.user_api.service;

import com.asusoftware.user_api.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${external-link.url}")
    private String externalImagesLink;

    public String uploadImage(MultipartFile file, UUID id) {
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uploadPath = uploadDir + id;

            // Ensure directory exists
            Path uploadDirPath = Paths.get(uploadPath);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // Delete old image if it exists
            Path oldFilePath = uploadDirPath.resolve(fileName);
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath); // Delete the old image
            }

            // Save the new file
            Path targetLocation = uploadDirPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the full URL where the image is accessible
            return externalImagesLink + id + "/" + fileName;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

}

