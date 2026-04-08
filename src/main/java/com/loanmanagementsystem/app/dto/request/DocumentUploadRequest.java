package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.DocumentType;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadRequest {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document number is required")
    @Size(max = 50, message = "Document number must not exceed 50 characters")
    private String documentNumber;

    @NotBlank(message = "Document name is required")
    @Size(max = 100, message = "Document name must not exceed 100 characters")
    private String documentName;

    @NotNull(message = "Document file is required")
    private MultipartFile file;
}