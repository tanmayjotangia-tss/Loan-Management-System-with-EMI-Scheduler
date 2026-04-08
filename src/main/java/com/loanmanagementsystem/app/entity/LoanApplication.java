package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"borrower", "reviewedByOfficer", "loan"})
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Loan type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @NotNull(message = "Borrower is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Requested amount must be positive")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @NotNull(message = "Requested tenure is required")
    @Positive(message = "Requested tenure must be positive")
    @Column(name = "requested_tenure_months", nullable = false)
    private Integer requestedTenureMonths;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @DecimalMin(value = "0.0", message = "Current EMI cannot be negative")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "current_emi", precision = 15, scale = 2)
    private BigDecimal currentEmi;

    @DecimalMin(value = "0.0", message = "DTI cannot be negative")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "calculated_dti", precision = 5, scale = 2)
    private BigDecimal calculatedDti;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category")
    private RiskCategory riskCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_strategy")
    private StrategyType suggestedStrategy;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_strategy")
    private StrategyType finalStrategy;

    @NotNull(message = "Status is required")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanApplicationStatus status = LoanApplicationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_officer_id")
    private LoanOfficer reviewedByOfficer;

    @Size(max = 500, message = "Officer comment cannot exceed 500 characters")
    @Column(name = "officer_comment")
    private String officerComment;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL)
    private Loan loan;
}