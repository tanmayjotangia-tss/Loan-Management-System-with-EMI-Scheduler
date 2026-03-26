package com.loanmanagementsystem.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanEligibilityRequest {

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Existing EMI is required")
    @DecimalMin(value = "0.0", message = "Existing EMI cannot be negative")
    private BigDecimal existingEmi;

    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 900, message = "Credit score cannot exceed 900")
    private Integer creditScore;
}
