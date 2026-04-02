package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.EmiResponse;
import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.service.EmiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emis")
@RequiredArgsConstructor
@Validated
public class EmiController {
    private EmiService emiService;

    @GetMapping("/{loanId}/unpaid")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<List<EmiResponse>>> getAllUnpaidEmis(
            @PathVariable Long loanId) {
        List<EmiResponse> responses = emiService.getUnpaidEmis(loanId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<List<EmiResponse>>> getAllLoans(
            @PathVariable Long loanId) {
        List<EmiResponse> responses = emiService.getAllEmis(loanId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
