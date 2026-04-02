package com.loanmanagementsystem.app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImplementation implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String contentType = file.getContentType();
            String filename = file.getOriginalFilename();

            if (contentType == null || filename == null) {
                throw new RuntimeException("Invalid file");
            }

            boolean isPdf = contentType.equals("application/pdf") &&
                    filename.toLowerCase().endsWith(".pdf");

            boolean isImage = contentType.startsWith("image/");

            if (!isPdf && !isImage) {
                throw new RuntimeException("Only PDF and Image files are allowed");
            }

            String resourceType = isPdf ? "raw" : "image";

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", resourceType)
            );

            Object url = uploadResult.get("secure_url");

            if (url == null) {
                throw new RuntimeException("Cloudinary upload failed");
            }
            return url.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
}