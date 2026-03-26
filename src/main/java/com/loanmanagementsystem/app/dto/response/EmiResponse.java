package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiResponse {

    private Long id;
    private Long loanId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal emiAmount;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal remainingBalance;
    private EmiStatus status;
    private LocalDate paymentDate;
    private BigDecimal paidAmount;
}