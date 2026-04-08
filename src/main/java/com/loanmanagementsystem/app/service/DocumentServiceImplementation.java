package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.DocumentUploadRequest;
import com.loanmanagementsystem.app.dto.response.DocumentResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.Document;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import com.loanmanagementsystem.app.exception.AlreadyExistsException;
import com.loanmanagementsystem.app.exception.AuthenticatedUserNotFoundException;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.mapper.DocumentMapper;
import com.loanmanagementsystem.app.repository.DocumentRepository;
import com.loanmanagementsystem.app.repository.LoanOfficerRepository;
import com.loanmanagementsystem.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImplementation implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final LoanOfficerRepository loanOfficerRepository;
    private final DocumentMapper documentMapper;
    private final CloudinaryServiceImplementation cloudinaryService;
    private final AuditService auditService;


    @Override
    public DocumentResponse uploadDocument(Long userId, DocumentUploadRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException());

        if (documentRepository.existsByUserIdAndDocumentType(userId, request.getDocumentType())) {
            throw new AlreadyExistsException("Document",request.getDocumentNumber());
        }

        String fileUrl = cloudinaryService.uploadFile(request.getFile());

        Document document = documentMapper.toEntity(request);
        document.setUser(user);
        document.setDocumentUrl(fileUrl);
        document.setIsVerified(false);
        document.setUploadedAt(LocalDateTime.now());

        Document saved = documentRepository.save(document);
        auditService.logAction(user.getId(), EntityType.USER,document.getId(), AuditAction.CREATED,"Document Uploaded: Unverified");

        return documentMapper.toResponse(saved);
    }

    @Override
    public List<DocumentResponse> getDocumentsByUserId(Long userId) {

        List<Document> documents = documentRepository.findAllByUserId(userId);

        return documentMapper.toResponseList(documents);
    }

    @Override
    public DocumentResponse getDocumentById(Long userId, Long documentId) {

        Document document = documentRepository.findByIdAndUserId(documentId, userId)
                .orElseThrow(() -> new BadRequestException("Document not found"));

        return documentMapper.toResponse(document);
    }

    @Transactional
    @Override
    public DocumentResponse verifyDocument(Long documentId, Long officerId) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BadRequestException("Document not found"));

        if (Boolean.TRUE.equals(document.getIsVerified())) {
            throw new BadRequestException("Document already verified");
        }

        LoanOfficer officer = loanOfficerRepository.findById(officerId)
                .orElseThrow(() -> new BadRequestException("Loan officer not found"));

        User borrower= document.getUser();
        borrower.setIsVerified(true);
        document.setIsVerified(true);
        document.setVerifiedByOfficer(officer);
        document.setVerifiedAt(LocalDateTime.now());

        userRepository.save(borrower);
        Document updated = documentRepository.save(document);

        auditService.logAction(borrower.getId(), EntityType.USER,document.getId(), AuditAction.STATUS_CHANGED,"Document Verified");
        return documentMapper.toResponse(updated);
    }
}