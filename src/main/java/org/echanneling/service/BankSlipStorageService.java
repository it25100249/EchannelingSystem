package org.echanneling.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class BankSlipStorageService {

    private final Path uploadPath;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public BankSlipStorageService(
            @Value("${bank.slip.upload-dir}")
            String uploadDir) {

        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create bank slip upload directory",
                    e
            );
        }
    }


    // ==========================================
    // STORE BANK SLIP
    // ==========================================
    public String storeBankSlip(MultipartFile file) {

        // Check file exists
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bank slip file is required"
            );
        }

        // Check file type
        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType)) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG or PDF bank slips are allowed"
            );
        }

        // Get safe extension
        String extension = getExtension(contentType);

        // Generate our own filename
        // Do NOT trust user's original filename
        String generatedFileName =
                UUID.randomUUID().toString() + extension;

        Path targetLocation =
                uploadPath.resolve(generatedFileName)
                        .normalize();

        // Path safety check
        if (!targetLocation.startsWith(uploadPath)) {
            throw new IllegalArgumentException(
                    "Invalid bank slip file path"
            );
        }

        try {

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save bank slip",
                    e
            );
        }

        // Store only generated filename in database
        return generatedFileName;
    }


    // ==========================================
    // LOAD BANK SLIP
    // ==========================================
    public Resource loadBankSlip(String fileName) {

        try {

            Path filePath =
                    uploadPath.resolve(fileName)
                            .normalize();

            // Prevent access outside bank-slip folder
            if (!filePath.startsWith(uploadPath)) {
                throw new IllegalArgumentException(
                        "Invalid bank slip file path"
                );
            }

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() ||
                    !resource.isReadable()) {

                throw new RuntimeException(
                        "Bank slip file not found"
                );
            }

            return resource;

        } catch (MalformedURLException e) {

            throw new RuntimeException(
                    "Could not read bank slip file",
                    e
            );
        }
    }


    // ==========================================
    // GET FILE EXTENSION
    // ==========================================
    private String getExtension(String contentType) {

        return switch (contentType) {

            case "image/jpeg" -> ".jpg";

            case "image/png" -> ".png";

            case "application/pdf" -> ".pdf";

            default -> throw new IllegalArgumentException(
                    "Unsupported bank slip file type"
            );
        };
    }
}