package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import com.loanmanagementsystem.app.service.LoanService;
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
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Validated
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoanFromApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) StrategyType type) {
        LoanResponse response = loanService.createLoanFromApplication(applicationId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        List<LoanResponse> responses = loanService.getAllLoans();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }


    @GetMapping("/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(
            @PathVariable Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LoanResponse response = loanService.getLoanById(loanId);

        if (userDetails.getRole().name().equals("BORROWER")
                && !response.getBorrowerId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("You are not authorised to view this loan.");
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/borrower/{borrowerId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN') or " +
            "(hasRole('BORROWER') and #borrowerId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByBorrowerId(@PathVariable Long borrowerId) {
        List<LoanResponse> responses = loanService.getLoansByBorrowerId(borrowerId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/type/{loanType}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByType(@PathVariable LoanType loanType) {
        List<LoanResponse> responses = loanService.getLoansByType(loanType);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByStatus(@PathVariable LoanStatus status) {
        List<LoanResponse> responses = loanService.getLoansByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{loanId}/close")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<LoanResponse>> closeLoan(@PathVariable Long loanId) {
        LoanResponse response = loanService.closeLoan(loanId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
