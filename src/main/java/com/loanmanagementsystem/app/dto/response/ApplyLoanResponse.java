package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyLoanResponse {

    private Long id;
    private LoanType loanType;
    private BigDecimal requestedAmount;
    private Integer requestedTenureMonths;
    private LoanApplicationStatus status;
    private LocalDateTime appliedAt;
}