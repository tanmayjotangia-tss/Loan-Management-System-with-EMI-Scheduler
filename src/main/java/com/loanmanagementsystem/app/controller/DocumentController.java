package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.DocumentUploadRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.DocumentResponse;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import com.loanmanagementsystem.app.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
public class DocumentController {

    private final DocumentService documentService;

    /**
     * BORROWER can only upload documents for themselves.
     * ADMIN / LOAN_OFFICER can upload on behalf of any user.
     */
    @PostMapping(value = "/user/{userId}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER', 'BORROWER')")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable Long userId,
            @Valid @ModelAttribute DocumentUploadRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails.getRole().name().equals("BORROWER")
                && !userDetails.getUserId().equals(userId)) {
            throw new AccessDeniedException("You can only upload documents for your own account.");
        }

        DocumentResponse response = documentService.uploadDocument(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER') or " +
            "(hasRole('BORROWER') and #userId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByUserId(@PathVariable Long userId) {
        List<DocumentResponse> responses = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{documentId}/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER') or " +
            "(hasRole('BORROWER') and #userId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(
            @PathVariable Long userId,
            @PathVariable Long documentId) {
        DocumentResponse response = documentService.getDocumentById(userId, documentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{documentId}/verify")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam Long officerId) {
        DocumentResponse response = documentService.verifyDocument(documentId, officerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}