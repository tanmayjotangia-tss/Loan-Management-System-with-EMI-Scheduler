package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "emis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"loan_id", "installment_number"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"loan", "payments", "penalties"})
public class Emi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "emi_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal emiAmount;

    @Column(name = "principal_component", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalComponent;

    @Column(name = "interest_component", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestComponent;

//    @Column(name = "remaining_balance", nullable = false, precision = 15, scale = 2)
//    private BigDecimal remainingBalance;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmiStatus status = EmiStatus.UPCOMING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Column(name = "overdue_marked", nullable = false)
    private boolean overdueMarked = false;

    @Column(name = "missed_emi_marked", nullable = false)
    private boolean missedEmiMarked = false;

    @Column(name = "last_overdue_alert_day")
    private Integer lastOverdueAlertDay = 0;

    @Builder.Default
    @OneToMany(mappedBy = "emi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "emi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Penalty> penalties = new ArrayList<>();
}