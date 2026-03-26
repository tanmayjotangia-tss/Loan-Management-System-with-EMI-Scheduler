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

    private Long id;
    private Long loanId;
    private Long emiId;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private LocalDateTime paidAt;
}
