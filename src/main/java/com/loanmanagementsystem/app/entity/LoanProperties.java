package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.LoanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoanProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Loan type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false, unique = true)
    private LoanType loanType;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be positive")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum amount must be positive")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum amount must be positive")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @NotNull(message = "Minimum tenure is required")
    @Positive(message = "Minimum tenure must be positive")
    @Column(name = "min_tenure", nullable = false)
    private Integer minTenure;

    @NotNull(message = "Maximum tenure is required")
    @Positive(message = "Maximum tenure must be positive")
    @Column(name = "max_tenure", nullable = false)
    private Integer maxTenure;

    @NotNull(message = "Late payment penalty is required")
    @DecimalMin(value = "0.0", message = "Penalty cannot be negative")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "late_payment_penalty_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal latePaymentPenaltyPercentage;

    @NotNull(message = "Missed EMI penalty is required")
    @DecimalMin(value = "0.0", message = "Penalty cannot be negative")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "missed_emi_penalty_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal missedEmiPenaltyPercentage;

    @NotNull(message = "Grace period is required")
    @Min(value = 0, message = "Grace period cannot be negative")
    @Column(name = "grace_period_days", nullable = false)
    private Integer gracePeriodDays;

    @NotNull(message = "Minimum CIBIL score is required")
    @Min(value = 300, message = "CIBIL score must be at least 300")
    @Max(value = 900, message = "CIBIL score cannot exceed 900")
    @Column(name = "min_required_cibil_score", nullable = false)
    private Integer minRequiredCibilScore;

    @NotNull
    @Column(name = "foreclosure_allowed", nullable = false)
    private Boolean foreclosureAllowed;

    @NotNull(message = "Minimum EMI before foreclosure is required")
    @Min(value = 0, message = "Minimum EMI cannot be negative")
    @Column(name = "min_emi_before_foreclosure", nullable = false)
    private Integer minEmiBeforeForeclosure;

    @NotNull(message = "Foreclosure penalty is required")
    @DecimalMin(value = "0.0", message = "Penalty cannot be negative")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "foreclosure_penalty_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal foreclosurePenaltyPercent;
}