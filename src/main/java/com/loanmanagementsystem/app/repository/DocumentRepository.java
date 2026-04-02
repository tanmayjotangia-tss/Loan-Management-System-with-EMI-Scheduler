package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Document;
import com.loanmanagementsystem.app.entity.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByUserId(Long userId);

    Optional<Document> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndDocumentType(Long userId, DocumentType documentType);
}