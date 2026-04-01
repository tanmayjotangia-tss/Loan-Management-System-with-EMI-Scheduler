package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanOfficerResponse;
import com.loanmanagementsystem.app.entity.enums.OfficerType;
import com.loanmanagementsystem.app.service.LoanOfficerService;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/officers")
@RequiredArgsConstructor
@Validated
public class LoanOfficerController {

    private final LoanOfficerService loanOfficerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanOfficerResponse>>> getAllLoanOfficers() {
        List<LoanOfficerResponse> responses = loanOfficerService.getAllLoanOfficers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanOfficerResponse>>> getAvailableLoanOfficers() {
        List<LoanOfficerResponse> responses = loanOfficerService.getAvailableLoanOfficers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<LoanOfficerResponse>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        LoanOfficerResponse response = loanOfficerService.getLoanOfficerById(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LoanOfficerResponse>> getLoanOfficerById(@PathVariable Long id) {
        LoanOfficerResponse response = loanOfficerService.getLoanOfficerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/availability")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> updateMyAvailability(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Boolean isAvailable) {
        loanOfficerService.updateLoanOfficerAvailability(userDetails.getUserId(), isAvailable);
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully."));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateLoanOfficerAvailability(
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {
        loanOfficerService.updateLoanOfficerAvailability(id, isAvailable);
        return ResponseEntity.ok(ApiResponse.success("Loan officer availability updated successfully."));
    }

    @PatchMapping("/{id}/type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateLoanOfficerType(
            @PathVariable Long id,
            @RequestParam OfficerType type) {
        loanOfficerService.updateLoanOfficerType(id, type);
        return ResponseEntity.ok(ApiResponse.success("Loan officer type updated successfully."));
    }
}
