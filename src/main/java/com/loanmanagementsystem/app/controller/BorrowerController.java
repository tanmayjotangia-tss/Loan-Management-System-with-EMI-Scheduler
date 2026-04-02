package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.UpdateUserRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.BorrowerResponse;
import com.loanmanagementsystem.app.service.BorrowerService;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowers")
@RequiredArgsConstructor
@Validated
public class BorrowerController {

    private final BorrowerService borrowerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BorrowerResponse>>> getAllBorrowers() {
        List<BorrowerResponse> responses = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<BorrowerResponse>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BorrowerResponse response = borrowerService.getBorrowerById(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BorrowerResponse>> getBorrowerById(@PathVariable Long id) {
        BorrowerResponse response = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest request) {
        borrowerService.updateBorrower(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully."));
    }
}
