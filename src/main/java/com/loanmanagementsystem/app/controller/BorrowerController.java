package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.UpdateUserRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.BorrowerResponse;
import com.loanmanagementsystem.app.service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<BorrowerResponse>>> getAllBorrowers() {
        List<BorrowerResponse> responses = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowerResponse>> getBorrowerById(@PathVariable Long id) {
        BorrowerResponse response = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateBorrower(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        borrowerService.updateBorrower(id, request);
        return ResponseEntity.ok(ApiResponse.success("Borrower updated successfully."));
    }
}
