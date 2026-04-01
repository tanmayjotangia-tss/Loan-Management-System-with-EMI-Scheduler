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


//    Only the BORROWER who owns the loan may pay an EMI.

    @PostMapping("/emi")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> makeEmiPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        verifyLoanOwnership(request.getLoanId(), userDetails.getUserId());

        PaymentResponse response = paymentService.makeEmiPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

//    Only the BORROWER who owns the loan may make a foreclosure payment.
    @PostMapping("/foreclosure")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> makeForeclosurePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        verifyLoanOwnership(request.getLoanId(), userDetails.getUserId());

        PaymentResponse response = paymentService.makeForeclosurePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

//      BORROWER can only view payments for their own loan.
//      LOAN_OFFICER / ADMIN can view payments for any loan.

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByLoanId(
            @PathVariable Long loanId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails.getRole().name().equals("BORROWER")) {
            verifyLoanOwnership(loanId, userDetails.getUserId());
        }

        List<PaymentResponse> responses = paymentService.getPaymentsByLoanId(loanId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }


//      BORROWER can only view payments for EMIs that belong to their own loan.
//      LOAN_OFFICER / ADMIN can view payments for any EMI.
    @GetMapping("/emi/{emiId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'BORROWER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByEmiId(
            @PathVariable Long emiId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PaymentResponse> responses = paymentService.getPaymentsByEmiId(emiId);

        // For BORROWER: infer the loan from the first payment result and verify ownership.
        if (userDetails.getRole().name().equals("BORROWER") && !responses.isEmpty()) {
            verifyLoanOwnership(responses.get(0).getLoanId(), userDetails.getUserId());
        }

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    private void verifyLoanOwnership(Long loanId, Long authenticatedUserId) {
        LoanResponse loan = loanService.getLoanById(loanId);
        if (!loan.getBorrowerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You are not authorised to perform this action on loan #" + loanId);
        }
    }
}
