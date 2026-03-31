package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanApplicationResponse;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan-applications")
@RequiredArgsConstructor
@Validated
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @PostMapping("/borrower/{borrowerId}")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> applyForLoan(
            @PathVariable Long borrowerId,
            @Valid @RequestBody LoanApplicationRequest request) {
        LoanApplicationResponse response = loanApplicationService.applyForLoan(borrowerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getApplicationById(@PathVariable Long applicationId) {
        LoanApplicationResponse response = loanApplicationService.getApplicationById(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/borrower/{borrowerId}")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getApplicationsByBorrowerId(@PathVariable Long borrowerId) {
        List<LoanApplicationResponse> responses = loanApplicationService.getApplicationsByBorrowerId(borrowerId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getAllPendingApplications() {
        List<LoanApplicationResponse> responses = loanApplicationService.getAllPendingApplications();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/pending/type/{loanType}")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getPendingApplicationsByType(@PathVariable LoanType loanType) {
        List<LoanApplicationResponse> responses = loanApplicationService.getPendingApplicationsByType(loanType);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{applicationId}/review")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getApplicationForReview(@PathVariable Long applicationId) {
        LoanApplicationResponse response = loanApplicationService.getApplicationForReview(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{applicationId}/suggest-strategy")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> suggestStrategy(@PathVariable Long applicationId) {
        LoanApplicationResponse response = loanApplicationService.suggestStrategy(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{applicationId}/review")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> reviewApplication(
            @PathVariable Long applicationId,
            @RequestParam Long officerId,
            @Valid @RequestBody LoanReviewRequest request) {
        LoanApplicationResponse response = loanApplicationService.reviewApplication(applicationId, officerId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
