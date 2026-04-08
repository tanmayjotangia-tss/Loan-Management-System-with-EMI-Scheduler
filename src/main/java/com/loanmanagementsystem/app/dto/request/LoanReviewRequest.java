package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanReviewRequest {

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private LoanApplicationStatus status;

    private StrategyType finalStrategy;

    @Size(max = 500, message = "Officer comment cannot exceed 500 characters")
    private String officerComment;
}