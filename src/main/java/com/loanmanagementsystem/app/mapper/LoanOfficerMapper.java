package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.LoanOfficerResponse;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LoanOfficerMapper {

    LoanOfficerResponse toResponse(LoanOfficer loanOfficer);
}
