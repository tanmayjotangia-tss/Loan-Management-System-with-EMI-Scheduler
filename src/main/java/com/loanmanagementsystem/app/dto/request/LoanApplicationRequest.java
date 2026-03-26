package com.loanmanagementsystem.app.dto.request;

import com.loanmanagementsystem.app.entity.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum loan amount is 1000")
    private BigDecimal requestedAmount;

    @NotNull(message = "Requested tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 360, message = "Tenure cannot exceed 360 months")
    private Integer requestedTenureMonths;

    private String purpose;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    private BigDecimal monthlyIncome;

    @DecimalMin(value = "0.0", message = "Current EMI cannot be negative")
    private BigDecimal currentEmi;
}
