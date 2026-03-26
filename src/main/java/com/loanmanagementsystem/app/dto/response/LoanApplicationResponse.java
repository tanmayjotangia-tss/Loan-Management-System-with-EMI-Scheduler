package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationResponse {

    private Long id;
    private LoanType loanType;
    private Long borrowerId;
    private String borrowerName;
    private BigDecimal requestedAmount;
    private Integer requestedTenureMonths;
    private String purpose;
    private BigDecimal monthlyIncome;
    private BigDecimal currentEmi;
    private BigDecimal calculatedDti;
    private RiskCategory riskCategory;
    private StrategyType suggestedStrategy;
    private StrategyType finalStrategy;
    private LoanApplicationStatus status;
    private Long reviewedByOfficerId;
    private String reviewedByOfficerName;
    private String officerComment;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
}