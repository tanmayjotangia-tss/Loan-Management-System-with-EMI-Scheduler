package com.loanmanagementsystem.app.service;

import java.util.List;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.ApplyLoanResponse;
import com.loanmanagementsystem.app.dto.response.LoanApplicationForReviewResponse;
import com.loanmanagementsystem.app.dto.response.PendingLoanApplicationResponse;
import com.loanmanagementsystem.app.dto.response.ReviewedLoanApplicationResponse;
import com.loanmanagementsystem.app.entity.enums.LoanType;

public interface LoanApplicationService {
    ApplyLoanResponse applyForLoan(Long borrowerId, LoanApplicationRequest request);
    ReviewedLoanApplicationResponse getApplicationById(Long applicationId);
    List<ReviewedLoanApplicationResponse> getApplicationsByBorrowerId(Long borrowerId);
    List<PendingLoanApplicationResponse> getAllPendingApplications();
    List<PendingLoanApplicationResponse> getPendingApplicationsByType(LoanType loanType);
    LoanApplicationForReviewResponse getApplicationForReview(Long applicationId);
    ReviewedLoanApplicationResponse suggestStrategy(Long applicationId);
    ReviewedLoanApplicationResponse reviewApplication(Long applicationId, Long officerId, LoanReviewRequest request);
    List<ReviewedLoanApplicationResponse> getCurrentUserApplications();
}