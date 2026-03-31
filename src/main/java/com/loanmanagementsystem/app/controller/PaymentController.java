package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;
import com.loanmanagementsystem.app.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/emi")
    public ResponseEntity<ApiResponse<PaymentResponse>> makeEmiPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.makeEmiPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/foreclosure")
    public ResponseEntity<ApiResponse<PaymentResponse>> makeForeclosurePayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.makeForeclosurePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByLoanId(@PathVariable Long loanId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByLoanId(loanId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/emi/{emiId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByEmiId(@PathVariable Long emiId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByEmiId(emiId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
