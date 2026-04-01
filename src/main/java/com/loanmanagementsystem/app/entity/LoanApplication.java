package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.*;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "requested_tenure_months", nullable = false)
    private Integer requestedTenureMonths;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "current_emi", precision = 15, scale = 2)
    private BigDecimal currentEmi;

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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanApplicationStatus status = LoanApplicationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_officer_id")
    private LoanOfficer reviewedByOfficer;

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
