package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.DocumentUploadRequest;
import com.loanmanagementsystem.app.dto.response.DocumentResponse;
import com.loanmanagementsystem.app.entity.Document;
import org.mapstruct.*;

import java.util.List;
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "documentUrl", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "verifiedByOfficer", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    Document toEntity(DocumentUploadRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "verifiedByOfficerId", source = "verifiedByOfficer.id")
    @Mapping(target = "verifiedByOfficerName", source = "verifiedByOfficer.name")
    DocumentResponse toResponse(Document document);

    List<DocumentResponse> toResponseList(List<Document> documents);
}