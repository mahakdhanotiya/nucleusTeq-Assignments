package com.mahak.capstone.interviewprocesstrackingsystem.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahak.capstone.interviewprocesstrackingsystem.constants.ApiConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.constants.ErrorConstants;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.ApiResponseDTO;

/**
 * File upload controller for resume PDFs.
 */
@RestController
@RequestMapping(ApiConstants.FILES)
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private static final String UPLOAD_DIR = "uploads";

    /**
     * Upload a PDF file.
     * POST /api/files/upload
     */
    @PostMapping(ApiConstants.UPLOAD)
    public ApiResponseDTO<java.util.Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();
        logger.info("File upload request received: {}", originalName);

        // Validate file type
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            logger.warn("Invalid file type rejected: {}", originalName);
            return new ApiResponseDTO<>(false, ErrorConstants.INVALID_FILE_TYPE, null);
        }

        // Create uploads directory
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save with unique name
        String fileName = UUID.randomUUID() + "_" + originalName;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File uploaded successfully: {}", fileName);

        String fileUrl = "http://localhost:8080/uploads/" + fileName;

        return new ApiResponseDTO<>(true, ApiConstants.FILE_UPLOADED,
                java.util.Map.of("url", fileUrl, "fileName", originalName));
    }
}
