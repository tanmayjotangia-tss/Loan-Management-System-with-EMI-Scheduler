package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {

    private Long id;
    private LoanType loanType;
    private Long loanApplicationId;
    private Long borrowerId;
    private String borrowerName;
    private Long approvedByOfficerId;
    private String approvedByOfficerName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private StrategyType strategyType;
    private BigDecimal totalPayableAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LoanStatus status;
    private Integer gracePeriodDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
