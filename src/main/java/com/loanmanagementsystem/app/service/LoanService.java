package com.loanmanagementsystem.app.service;

import java.util.List;

import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;

public interface LoanService {
    LoanResponse createLoanFromApplication(Long applicationId);
    List<LoanResponse> getAllLoans();
    LoanResponse getLoanById(Long loanId);
    List<LoanResponse> getLoansByBorrowerId(Long borrowerId);
    List<LoanResponse> getLoansByType(LoanType loanType);
    List<LoanResponse> getLoansByStatus(LoanStatus status);
    LoanResponse closeLoan(Long loanId);
    LoanResponse processForeclosure(Long loanId);
}
