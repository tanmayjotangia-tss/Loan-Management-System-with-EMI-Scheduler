package com.loanmanagementsystem.app.service;

import java.util.List;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.LoanApplicationResponse;
import com.loanmanagementsystem.app.entity.enums.LoanType;

public interface LoanApplicationService {
    LoanApplicationResponse applyForLoan(Long borrowerId, LoanApplicationRequest request);
    LoanApplicationResponse getApplicationById(Long applicationId);
    List<LoanApplicationResponse> getApplicationsByBorrowerId(Long borrowerId);
    List<LoanApplicationResponse> getAllPendingApplications();
    List<LoanApplicationResponse> getPendingApplicationsByType(LoanType loanType);
    LoanApplicationResponse getApplicationForReview(Long applicationId);
    LoanApplicationResponse suggestStrategy(Long applicationId);
    LoanApplicationResponse reviewApplication(Long applicationId, Long officerId, LoanReviewRequest request);
    List<LoanApplicationResponse> getCurrentUserApplications();
}