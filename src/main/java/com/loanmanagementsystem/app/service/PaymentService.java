package com.loanmanagementsystem.app.service;

import java.math.BigDecimal;
import java.util.List;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse makeEmiPayment(PaymentRequest request);
    PaymentResponse makeForeclosurePayment(PaymentRequest request);
    List<PaymentResponse> getPaymentsByLoanId(Long loanId);
    List<PaymentResponse> getPaymentsByEmiId(Long emiId);
}
