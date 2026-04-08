package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.PaymentMode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long loanId;
    private Long installmentNumber;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private LocalDateTime paidAt;
}
