package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"loanApplication", "borrower", "approvedByOfficer", "emis", "payments", "penalties"})
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_officer_id", nullable = false)
    private LoanOfficer approvedByOfficer;

    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "emi_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal emiAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy_type", nullable = false)
    private StrategyType strategyType;

    @Column(name = "total_payable_amount", precision = 15, scale = 2)
    private BigDecimal totalPayableAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays;

    @Builder.Default
    @Column(name = "foreclosure_allowed", nullable = false)
    private Boolean foreclosureAllowed = false;

    @Column(name = "foreclosure_charges", precision = 15, scale = 2)
    private BigDecimal foreclosureCharges;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emi> emis = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Penalty> penalties = new ArrayList<>();
}
