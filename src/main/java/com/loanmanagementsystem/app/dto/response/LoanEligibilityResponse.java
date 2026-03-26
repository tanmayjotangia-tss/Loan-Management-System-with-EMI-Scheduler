package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.RiskCategory;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanEligibilityResponse {

    private boolean eligible;

    private RiskCategory riskCategory;

    private BigDecimal debtToIncomeRatio;

    private String message;

    private BigDecimal maxEligibleEmi;

    private Integer creditScore;

    private String creditScoreRemark;
}
