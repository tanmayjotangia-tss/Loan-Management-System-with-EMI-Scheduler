package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.PenaltyReason;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "penalties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"emi", "loan"})
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emi_id", nullable = false)
    private Emi emi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyReason reason;

    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate;

    @Builder.Default
    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    @Column(name = "paid_date")
    private LocalDate paidDate;
}
