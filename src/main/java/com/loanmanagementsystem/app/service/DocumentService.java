package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.DocumentUploadRequest;
import com.loanmanagementsystem.app.dto.response.DocumentResponse;

import java.util.List;

public interface DocumentService {

    DocumentResponse uploadDocument(Long userId, DocumentUploadRequest request);

    List<DocumentResponse> getDocumentsByUserId(Long userId);

    DocumentResponse getDocumentById(Long userId, Long documentId);

    DocumentResponse verifyDocument(Long documentId, Long officerId);
}
