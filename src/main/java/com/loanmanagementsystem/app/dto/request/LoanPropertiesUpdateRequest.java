package com.loanmanagementsystem.app.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPropertiesUpdateRequest {

    private BigDecimal interestRate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTenure;
    private Integer maxTenure;
    private BigDecimal latePaymentPenaltyPercentage;
    private BigDecimal missedEmiPenaltyPercentage;
    private Integer gracePeriodDays;
    private Integer minRequiredCibilScore;
    private Boolean foreclosureAllowed;
    private Integer minEmiBeforeForeclosure;
    private BigDecimal foreclosurePenaltyPercent;
}