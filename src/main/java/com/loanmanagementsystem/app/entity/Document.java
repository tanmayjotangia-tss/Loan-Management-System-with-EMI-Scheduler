package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.DocumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "verifiedByOfficer"})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Document type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @NotBlank(message = "Document number is required")
    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @NotBlank(message = "Document name is required")
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @NotBlank(message = "Document URL is required")
    @Column(name = "document_url", nullable = false)
    private String documentUrl;

    @Builder.Default
    @NotNull(message = "Verification status is required")
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_officer_id")
    private LoanOfficer verifiedByOfficer;

    @NotNull(message = "Upload time is required")
    @PastOrPresent(message = "Upload time cannot be in the future")
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PastOrPresent(message = "Verification time cannot be in the future")
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}