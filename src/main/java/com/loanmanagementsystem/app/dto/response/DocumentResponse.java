package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.DocumentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long id;
    private Long userId;
    private DocumentType documentType;
    private String documentNumber;
    private String documentName;
    private String documentUrl;
    private Boolean isVerified;
    private Long verifiedByOfficerId;
    private String verifiedByOfficerName;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
}