package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.LoanApplicationForReviewResponse;
import com.loanmanagementsystem.app.entity.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface LoanApplicationForReviewMapper {
    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerName", source = "borrower.name")
    LoanApplicationForReviewResponse toResponse(LoanApplication loanApplication);
}
