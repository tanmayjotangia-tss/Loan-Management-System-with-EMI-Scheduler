package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.PaymentMode;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @Positive(message = "Installment number must be positive")
    private Long installmentNumber; // nullable for foreclosure

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;
}