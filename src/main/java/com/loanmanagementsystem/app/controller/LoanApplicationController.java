package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.*;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import com.loanmanagementsystem.app.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan-applications")
@RequiredArgsConstructor
@Validated
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<ApplyLoanResponse>> applyForLoan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LoanApplicationRequest request) {

        ApplyLoanResponse response =
                loanApplicationService.applyForLoan(userDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Loan application submitted successfully", response));
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<List<ReviewedLoanApplicationResponse>>> getMyApplications() {

        List<ReviewedLoanApplicationResponse> responses =
                loanApplicationService.getCurrentUserApplications();

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan applications fetched successfully", responses)
        );
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('BORROWER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReviewedLoanApplicationResponse>> getApplicationById(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ReviewedLoanApplicationResponse response =
                loanApplicationService.getApplicationById(applicationId);

        if (userDetails.getRole().name().equals("BORROWER")
                && !response.getBorrowerId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("You are not authorised to view this application.");
        }

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan application fetched successfully", response)
        );
    }

    @GetMapping("/borrower/{borrowerId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewedLoanApplicationResponse>>> getApplicationsByBorrowerId(
            @PathVariable Long borrowerId) {

        List<ReviewedLoanApplicationResponse> responses =
                loanApplicationService.getApplicationsByBorrowerId(borrowerId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan applications fetched successfully", responses)
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PendingLoanApplicationResponse>>> getAllPendingApplications() {

        List<PendingLoanApplicationResponse> responses =
                loanApplicationService.getAllPendingApplications();

        return ResponseEntity.ok(
                ApiResponse.success(200, "Pending loan applications fetched successfully", responses)
        );
    }

    @GetMapping("/pending/type/{loanType}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PendingLoanApplicationResponse>>> getPendingApplicationsByType(
            @PathVariable LoanType loanType) {

        List<PendingLoanApplicationResponse> responses =
                loanApplicationService.getPendingApplicationsByType(loanType);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Pending loan applications for type fetched successfully", responses)
        );
    }

    @GetMapping("/{applicationId}/review")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanApplicationForReviewResponse>> getApplicationForReview(
            @PathVariable Long applicationId) {

        LoanApplicationForReviewResponse response =
                loanApplicationService.getApplicationForReview(applicationId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan application fetched for review", response)
        );
    }

    @PostMapping("/{applicationId}/review")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<ReviewedLoanApplicationResponse>> reviewApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LoanReviewRequest request) {

        ReviewedLoanApplicationResponse response =
                loanApplicationService.reviewApplication(
                        applicationId,
                        userDetails.getUserId(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan application reviewed successfully", response)
        );
    }
}