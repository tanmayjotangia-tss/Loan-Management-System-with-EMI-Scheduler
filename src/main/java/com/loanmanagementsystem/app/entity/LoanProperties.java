package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.LoanType;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false, unique = true)
    private LoanType loanType;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "min_tenure", nullable = false)
    private Integer minTenure;

    @Column(name = "max_tenure", nullable = false)
    private Integer maxTenure;

    @Column(name = "late_payment_penalty_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal latePaymentPenaltyPercentage;

    @Column(name = "missed_emi_penalty_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal missedEmiPenaltyPercentage;

    @Column(name = "grace_period_days", nullable = false)
    private Integer gracePeriodDays;

    @Column(name = "min_required_cibil_score", nullable = false)
    private Integer minRequiredCibilScore;
}