package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.RegisterBorrowerRequest;
import com.loanmanagementsystem.app.dto.response.BorrowerResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BorrowerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "loanApplications", ignore = true)
    @Mapping(target = "loans", ignore = true)
    Borrower toEntity(RegisterBorrowerRequest request);

    BorrowerResponse toResponse(Borrower borrower);

    List<BorrowerResponse> toResponseList(List<Borrower> borrowers);
}
