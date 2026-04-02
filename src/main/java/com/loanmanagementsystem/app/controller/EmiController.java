package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.EmiResponse;
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
    private final EmiService emiService;

    @GetMapping("/borrower/{borrowerId}/loan/{loanId}/unpaid")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER') or " +
            "(hasRole('BORROWER') and #borrowerId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<List<EmiResponse>>> getAllUnpaidEmis(
            @PathVariable Long borrowerId,
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                ApiResponse.success(emiService.getUnpaidEmis(loanId))
        );
    }

    @GetMapping("/borrower/{borrowerId}/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN') or " +
            "(hasRole('BORROWER') and #borrowerId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<List<EmiResponse>>> getEmis(
            @PathVariable Long borrowerId,
            @PathVariable Long loanId) {
        return ResponseEntity.ok(ApiResponse.success(emiService.getAllEmis(loanId)));
    }
}
