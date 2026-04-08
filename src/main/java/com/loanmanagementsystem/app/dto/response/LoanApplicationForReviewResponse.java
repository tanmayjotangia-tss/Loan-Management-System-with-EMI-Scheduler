package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.RiskCategory;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationForReviewResponse {
    private Long id;
    private LoanType loanType;
    private Long borrowerId;
    private String borrowerName;
    private BigDecimal requestedAmount;
    private Integer requestedTenureMonths;
    private BigDecimal monthlyIncome;
    private RiskCategory riskCategory;
    private StrategyType suggestedStrategy;
    private LocalDateTime appliedAt;
}
