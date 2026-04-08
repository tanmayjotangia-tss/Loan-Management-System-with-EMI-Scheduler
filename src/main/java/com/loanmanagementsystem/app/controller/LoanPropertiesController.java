package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.service.LoanPropertiesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loan-properties")
@RequiredArgsConstructor
@Validated
public class LoanPropertiesController {

    private final LoanPropertiesService loanPropertiesService;

    @GetMapping("/{loanType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LoanProperties>> getLoanProperties(
            @PathVariable LoanType loanType) {

        LoanProperties response = loanPropertiesService.getLoanProperties(loanType);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan properties fetched successfully", response)
        );
    }

    @PutMapping("/{loanType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanProperties>> updateLoanProperties(
            @PathVariable LoanType loanType,
            @Valid @RequestBody LoanProperties updatedProperties) {

        LoanProperties response =
                loanPropertiesService.updateLoanProperties(loanType, updatedProperties);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Loan properties updated successfully", response)
        );
    }
}