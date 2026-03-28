package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanType;

public interface LoanPropertiesService {
    LoanProperties updateLoanProperties(LoanType loanType, LoanProperties updatedProperties);
    LoanProperties displayLoanProperties(LoanType loanType);
}
