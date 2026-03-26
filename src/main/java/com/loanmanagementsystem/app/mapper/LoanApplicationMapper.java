package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.response.LoanApplicationResponse;
import com.loanmanagementsystem.app.entity.LoanApplication;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

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

    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerName", source = "borrower.name")
    @Mapping(target = "reviewedByOfficerId", source = "reviewedByOfficer.id")
    @Mapping(target = "reviewedByOfficerName", source = "reviewedByOfficer.name")
    LoanApplicationResponse toResponse(LoanApplication loanApplication);
}
