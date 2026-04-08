package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.response.ApplyLoanResponse;
import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ApplyLoanMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borrower", ignore = true)
    @Mapping(target = "calculatedDti", ignore = true)
    @Mapping(target = "riskCategory", ignore = true)
    @Mapping(target = "suggestedStrategy", ignore = true)
    @Mapping(target = "finalStrategy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reviewedByOfficer", ignore = true)
    @Mapping(target = "officerComment", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "loan", ignore = true)
    LoanApplication toEntity(LoanApplicationRequest request);
    ApplyLoanResponse toResponse(LoanApplication loanApplication);
}
