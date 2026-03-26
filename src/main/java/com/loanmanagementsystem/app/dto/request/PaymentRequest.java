package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    private Long emiId; // nullable for foreclosure

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;
}
