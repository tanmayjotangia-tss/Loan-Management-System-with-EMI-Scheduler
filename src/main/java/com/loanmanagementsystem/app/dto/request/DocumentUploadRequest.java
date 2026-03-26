package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadRequest {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document number is required")
    private String documentNumber;

    @NotBlank(message = "Document name is required")
    private String documentName;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;
}
