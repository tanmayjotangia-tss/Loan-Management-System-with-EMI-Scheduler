package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanOfficerResponse;
import com.loanmanagementsystem.app.entity.enums.OfficerType;
import com.loanmanagementsystem.app.service.LoanOfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<LoanOfficerResponse>>> getAllLoanOfficers() {
        List<LoanOfficerResponse> responses = loanOfficerService.getAllLoanOfficers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<LoanOfficerResponse>>> getAvailableLoanOfficers() {
        List<LoanOfficerResponse> responses = loanOfficerService.getAvailableLoanOfficers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanOfficerResponse>> getLoanOfficerById(@PathVariable Long id) {
        LoanOfficerResponse response = loanOfficerService.getLoanOfficerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<String>> updateLoanOfficerAvailability(
            @PathVariable Long id, 
            @RequestParam Boolean isAvailable) {
        loanOfficerService.updateLoanOfficerAvailability(id, isAvailable);
        return ResponseEntity.ok(ApiResponse.success("Loan officer availability updated successfully."));
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<ApiResponse<String>> updateLoanOfficerType(
            @PathVariable Long id, 
            @RequestParam OfficerType type) {
        loanOfficerService.updateLoanOfficerType(id, type);
        return ResponseEntity.ok(ApiResponse.success("Loan officer type updated successfully."));
    }
}
