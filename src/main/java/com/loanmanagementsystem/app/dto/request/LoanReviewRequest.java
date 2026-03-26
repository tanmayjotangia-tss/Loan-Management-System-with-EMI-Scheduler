package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanReviewRequest {

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private LoanApplicationStatus status;

    private StrategyType finalStrategy;

    private String officerComment;

    private BigDecimal interestRate;
}