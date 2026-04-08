package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewedLoanApplicationResponse {

    private Long id;
    private LoanType loanType;
    private Long borrowerId;
    private String borrowerName;
    private BigDecimal requestedAmount;
    private Integer requestedTenureMonths;
    private BigDecimal monthlyIncome;
    private RiskCategory riskCategory;
    private StrategyType finalStrategy;
    private LoanApplicationStatus status;
    private String reviewedByOfficerName;
    private String officerComment;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
}