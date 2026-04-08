package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.response.PendingLoanApplicationResponse;
import com.loanmanagementsystem.app.entity.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PendingLoanApplicationMapper {
    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerName", source = "borrower.name")
    PendingLoanApplicationResponse toResponse (LoanApplication loanApplication);
}