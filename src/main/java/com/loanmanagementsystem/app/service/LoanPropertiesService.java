package com.loanmanagementsystem.app.service;
import com.loanmanagementsystem.app.dto.request.LoanPropertiesUpdateRequest;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanType;

public interface LoanPropertiesService {
    LoanProperties updateLoanProperties(LoanType loanType, LoanPropertiesUpdateRequest updatedProperties);
    LoanProperties getLoanProperties(LoanType loanType);
}
