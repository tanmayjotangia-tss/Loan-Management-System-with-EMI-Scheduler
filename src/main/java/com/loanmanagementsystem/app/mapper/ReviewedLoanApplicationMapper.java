package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.response.ReviewedLoanApplicationResponse;
import com.loanmanagementsystem.app.entity.LoanApplication;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewedLoanApplicationMapper {

    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerName", source = "borrower.name")
    @Mapping(target = "reviewedByOfficerName", source = "reviewedByOfficer.name")
    ReviewedLoanApplicationResponse toResponse(LoanApplication loanApplication);
}
