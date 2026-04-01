package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanApplicationResponse;
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
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> applyForLoan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LoanApplicationRequest request) {
        LoanApplicationResponse response = loanApplicationService.applyForLoan(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LoanApplicationResponse> responses = loanApplicationService.getApplicationsByBorrowerId(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('BORROWER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getApplicationById(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LoanApplicationResponse response = loanApplicationService.getApplicationById(applicationId);

        if (userDetails.getRole().name().equals("BORROWER")
                && !response.getBorrowerId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("You are not authorised to view this application.");
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/borrower/{borrowerId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getApplicationsByBorrowerId(
            @PathVariable Long borrowerId) {
        List<LoanApplicationResponse> responses = loanApplicationService.getApplicationsByBorrowerId(borrowerId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getAllPendingApplications() {
        List<LoanApplicationResponse> responses = loanApplicationService.getAllPendingApplications();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/pending/type/{loanType}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getPendingApplicationsByType(
            @PathVariable LoanType loanType) {
        List<LoanApplicationResponse> responses = loanApplicationService.getPendingApplicationsByType(loanType);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{applicationId}/review")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getApplicationForReview(
            @PathVariable Long applicationId) {
        LoanApplicationResponse response = loanApplicationService.getApplicationForReview(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{applicationId}/suggest-strategy")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> suggestStrategy(
            @PathVariable Long applicationId) {
        LoanApplicationResponse response = loanApplicationService.suggestStrategy(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{applicationId}/review")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> reviewApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LoanReviewRequest request) {
        LoanApplicationResponse response = loanApplicationService.reviewApplication(
                applicationId, userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
