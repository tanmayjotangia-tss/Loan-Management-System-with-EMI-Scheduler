package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.Loan;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "loanApplicationId", source = "loanApplication.id")
    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerName", source = "borrower.name")
    @Mapping(target = "approvedByOfficerName", source = "approvedByOfficer.name")
    LoanResponse toResponse(Loan loan);
}
