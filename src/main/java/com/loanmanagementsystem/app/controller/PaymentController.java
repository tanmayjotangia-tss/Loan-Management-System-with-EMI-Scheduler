package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import com.loanmanagementsystem.app.service.LoanService;
import com.loanmanagementsystem.app.service.PaymentService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;
    private final LoanService loanService;

    @PostMapping("/emi")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> makeEmiPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        verifyLoanOwnership(request.getLoanId(), userDetails.getUserId());

        PaymentResponse response = paymentService.makeEmiPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "EMI payment completed successfully", response));
    }

    @PostMapping("/foreclosure")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> makeForeclosurePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        verifyLoanOwnership(request.getLoanId(), userDetails.getUserId());

        PaymentResponse response = paymentService.makeForeclosurePayment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Loan foreclosure payment completed successfully", response));
    }

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByLoanId(
            @PathVariable Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails.getRole().name().equals("BORROWER")) {
            verifyLoanOwnership(loanId, userDetails.getUserId());
        }

        List<PaymentResponse> responses = paymentService.getPaymentsByLoanId(loanId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Payments fetched successfully for loan", responses)
        );
    }

    @GetMapping("/emi/{emiId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByEmiId(
            @PathVariable Long emiId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PaymentResponse> responses = paymentService.getPaymentsByEmiId(emiId);

        if (userDetails.getRole().name().equals("BORROWER") && !responses.isEmpty()) {
            verifyLoanOwnership(responses.get(0).getLoanId(), userDetails.getUserId());
        }

        return ResponseEntity.ok(
                ApiResponse.success(200, "Payments fetched successfully for EMI", responses)
        );
    }

    private void verifyLoanOwnership(Long loanId, Long authenticatedUserId) {
        LoanResponse loan = loanService.getLoanById(loanId);
        if (!loan.getBorrowerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You are not authorised to perform this action on loan #" + loanId);
        }
    }
}