package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.LoanEligibilityResponse;
import com.loanmanagementsystem.app.entity.enums.RiskCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanEligibilityMapper {

    public LoanEligibilityResponse toResponse(boolean eligible,
                                               RiskCategory riskCategory,
                                               BigDecimal debtToIncomeRatio,
                                               String message,
                                               BigDecimal maxEligibleEmi,
                                               Integer creditScore,
                                               String creditScoreRemark) {

        return LoanEligibilityResponse.builder()
                .eligible(eligible)
                .riskCategory(riskCategory)
                .debtToIncomeRatio(debtToIncomeRatio)
                .message(message)
                .maxEligibleEmi(maxEligibleEmi)
                .creditScore(creditScore)
                .creditScoreRemark(creditScoreRemark)
                .build();
    }
}
