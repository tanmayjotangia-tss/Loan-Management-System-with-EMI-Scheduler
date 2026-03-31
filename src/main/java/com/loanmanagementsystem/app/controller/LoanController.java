package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import com.loanmanagementsystem.app.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<LoanResponse>> createLoanFromApplication(@PathVariable Long applicationId, @RequestParam StrategyType type) {
        LoanResponse response = loanService.createLoanFromApplication(applicationId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        List<LoanResponse> responses = loanService.getAllLoans();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(@PathVariable Long loanId) {
        LoanResponse response = loanService.getLoanById(loanId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/borrower/{borrowerId}")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByBorrowerId(@PathVariable Long borrowerId) {
        List<LoanResponse> responses = loanService.getLoansByBorrowerId(borrowerId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/type/{loanType}")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByType(@PathVariable LoanType loanType) {
        List<LoanResponse> responses = loanService.getLoansByType(loanType);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByStatus(@PathVariable LoanStatus status) {
        List<LoanResponse> responses = loanService.getLoansByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{loanId}/close")
    public ResponseEntity<ApiResponse<LoanResponse>> closeLoan(@PathVariable Long loanId) {
        LoanResponse response = loanService.closeLoan(loanId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
