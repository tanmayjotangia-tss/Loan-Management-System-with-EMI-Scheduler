package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.EmiResponse;
import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.service.EmiService;
import com.loanmanagementsystem.app.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emis")
@RequiredArgsConstructor
@Validated
public class EmiController {

    private final EmiService emiService;
    private final LoanService loanService;

    @GetMapping("/borrower/{borrowerId}/loan/{loanId}/unpaid")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER') or " +
            "(hasRole('BORROWER') and #borrowerId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<List<EmiResponse>>> getAllUnpaidEmis(
            @PathVariable Long borrowerId,
            @PathVariable Long loanId) {

        verifyLoanOwnership(loanId,borrowerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Unpaid EMIs fetched successfully",
                        emiService.getUnpaidEmis(loanId)
                )
        );
    }

    @GetMapping("/borrower/{borrowerId}/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN') or " +
            "(hasRole('BORROWER') and #borrowerId == authentication.principal.userId)")
    public ResponseEntity<ApiResponse<List<EmiResponse>>> getEmis(
            @PathVariable Long borrowerId,
            @PathVariable Long loanId) {

        verifyLoanOwnership(loanId,borrowerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Loan EMI schedule fetched successfully",
                        emiService.getAllEmis(loanId)
                )
        );
    }

    private void verifyLoanOwnership(Long loanId, Long borrowerId) {
        LoanResponse loan = loanService.getLoanById(loanId);
        if (!loan.getBorrowerId().equals(borrowerId)) {
            throw new AccessDeniedException(
                    "Loan #" + loanId + " does not belong to borrower #" + borrowerId);
        }
    }
}